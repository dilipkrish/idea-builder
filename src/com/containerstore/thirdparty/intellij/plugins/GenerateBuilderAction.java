package com.containerstore.thirdparty.intellij.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

public class GenerateBuilderAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        maybeEnablePresentation(e);
    }

    @Override
    public void update(AnActionEvent e) {
        maybeEnablePresentation(e);
    }

    private void maybeEnablePresentation(AnActionEvent e) {
        PsiClass psiClass = psiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
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
