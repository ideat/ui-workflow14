package com.mindware.workflow.ui.ui.components.detailsdrawer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Right;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.LumoStyles;
import com.mindware.workflow.ui.ui.util.UIUtils;

public class DetailsDrawerFooter extends FlexBoxLayout {

	private Button save;
	private Button cancel;
	private Button print;

	public DetailsDrawerFooter() {
		setBackgroundColor(LumoStyles.Color.Contrast._5);
		setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
		setSpacing(Right.S);
		setWidthFull();

		save = UIUtils.createPrimaryButton("Guardar");
		cancel = UIUtils.createTertiaryButton("Cancelar");
		print = UIUtils.createContrastButton("Imprimir");
		print.setVisible(false);
		add(save, cancel, print);
	}

	public void saveState(boolean state){
		save.setEnabled(state);
	}

	public Registration addSaveListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
		return save.addClickListener(listener);
	}

	public Registration addCancelListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
		return cancel.addClickListener(listener);
	}

	public void printVisible(boolean visible){
		print.setVisible(visible);
	}

	public Registration addPrintListener(ComponentEventListener<ClickEvent<Button>> listener){
		return print.addClickListener(listener);
	}

}
