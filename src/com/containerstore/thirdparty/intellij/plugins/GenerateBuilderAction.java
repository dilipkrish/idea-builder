package com.containerstore.thirdparty.intellij.plugins;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;

public class GenerateBuilderAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = maybeEnablePresentation(e);
        FieldSelectionDialog dialog = new FieldSelectionDialog(psiClass);
        dialog.show();
        if (dialog.isOK()) {
            CollectionListModel<PsiField> fields = dialog.getFields();
            //TODO:
            // 1. Generate the builder based on the selected fields
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
            PsiClass builderClass = builderClass(elementFactory, psiClass, fields);
            // 2. Add it to the file that is currently being edited
            writeChangesToFile(psiClass, builderClass);
            // 3. Add an accessor to the newly created builder
        }
    }

    private void writeChangesToFile(final PsiClass psiClass, final PsiClass builderClass) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                psiClass.add(builderClass);
            }
        }.execute();
    }

    private PsiClass builderClass(PsiElementFactory elementFactory, PsiClass parentClass, CollectionListModel<PsiField> fields) {
        String builderClassName = String.format("%sBuilder", parentClass.getName());
        PsiClass builderClass = elementFactory.createClass(builderClassName);
        PsiModifierList modifierList = builderClass.getModifierList();
        if (modifierList != null) {
            modifierList.setModifierProperty(PsiModifier.STATIC, true);
        }
        for (PsiField field : fields.getItems()) {
            builderClass.add(elementFactory.createField(field.getName(), field.getType()));
        }
        for (PsiField field : fields.getItems()) {
            StringBuilder sb = new StringBuilder();
            sb.append("public ").append(builderClassName).append(" with")
                    .append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field.getName())).append("(")
                    .append(field.getType().getCanonicalText())
                    .append(" ").append(field.getName())
                    .append(") {");
            sb.append(" this.").append(field.getName()).append(" = ").append(field.getName()).append(";");
            sb.append(System.getProperty("line.separator"));
            sb.append(" return this;");
            sb.append(System.getProperty("line.separator"));
            sb.append("}");
            sb.append(System.getProperty("line.separator"));
            builderClass.add(elementFactory.createMethodFromText(sb.toString(), builderClass));
        }
        return builderClass;
    }

    @Override
    public void update(AnActionEvent e) {
        maybeEnablePresentation(e);
    }

    private PsiClass maybeEnablePresentation(AnActionEvent e) {
        PsiClass psiClass = psiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
        return psiClass;
    }

    private PsiClass psiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass parentClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        if (parentClass == null) {
            return parentClass;
        }
        return parentClass;
    }
}
