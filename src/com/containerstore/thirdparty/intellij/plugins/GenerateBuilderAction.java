package com.containerstore.thirdparty.intellij.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
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
            // 2. Add it to the file that is currently being edited
            // 3. Add an accessor to the newly created builder
        }
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
