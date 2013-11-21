package com.containerstore.thirdparty.intellij.plugins;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

public class FieldSelectionDialog extends DialogWrapper {
    private CollectionListModel<PsiField> fields;
    private final JPanel panel;

    protected FieldSelectionDialog(PsiClass psiClass) {
        super(psiClass.getProject());


        JList list = createJListWithFields(psiClass);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(list);
        decorator.disableAddAction();
        panel = decorator.createPanel();

        setTitle("Select Fields For Generating The Builder");
        init();
    }

    private JList createJListWithFields(PsiClass psiClass) {
        fields = new CollectionListModel<PsiField>(psiClass.getAllFields());
        JList list = new JBList(fields);
        list.setCellRenderer(new DefaultPsiElementCellRenderer());
        return list;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public CollectionListModel<PsiField> getFields() {
        return fields;
    }
}
