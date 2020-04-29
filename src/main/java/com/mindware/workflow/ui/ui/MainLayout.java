package com.mindware.workflow.ui.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.entity.rol.Option;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.ui.views.applicant.ApplicantView;
import com.mindware.workflow.ui.ui.views.authorizerExceptions.AuthorizerExceptionsCreditRequestDtoView;
import com.mindware.workflow.ui.ui.views.cashFlow.CashFlowView;
import com.mindware.workflow.ui.ui.views.comercial.client.ClientView;
import com.mindware.workflow.ui.ui.views.config.authorizer.UserAuthorizerView;
import com.mindware.workflow.ui.ui.views.config.cityProvince.CityProvinceView;
import com.mindware.workflow.ui.ui.views.config.exceptions.ExceptionsView;
import com.mindware.workflow.ui.ui.views.config.exchangeRate.ExchangeRateView;
import com.mindware.workflow.ui.ui.views.config.office.OfficeView;
import com.mindware.workflow.ui.ui.views.config.office.SignatorieView;
import com.mindware.workflow.ui.ui.views.config.parameter.ParameterView;
import com.mindware.workflow.ui.ui.views.config.templateForms.TemplateFormsView;
import com.mindware.workflow.ui.ui.views.config.workflowProduct.WorflowProductView;
import com.mindware.workflow.ui.ui.views.contract.ContractCreditRequestDtoView;
import com.mindware.workflow.ui.ui.views.contract.ContractVariableView;
import com.mindware.workflow.ui.ui.views.contract.TemplateContractView;
import com.mindware.workflow.ui.ui.views.creditRequest.CreditRequestView;
import com.mindware.workflow.ui.ui.views.creditResolution.CreditResolutionCreditRequestDtoView;
import com.mindware.workflow.ui.ui.views.legal.LegalInformationView;
import com.mindware.workflow.ui.ui.views.observation.ObservationCreditRequestApplicantView;
import com.mindware.workflow.ui.ui.views.patrimonialStatement.CreditPatrimonialStatement;
import com.mindware.workflow.ui.ui.views.rol.RolView;
import com.mindware.workflow.ui.ui.views.stageHistory.StageHistoryGlobalView;
import com.mindware.workflow.ui.ui.views.stageHistory.StageHistoryView;
import com.mindware.workflow.ui.ui.views.templateObservation.TemplateObservationView;
import com.mindware.workflow.ui.ui.views.users.UsersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.*;
import com.vaadin.flow.theme.lumo.Lumo;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.components.navigation.bar.TabBar;
import com.mindware.workflow.ui.ui.components.navigation.drawer.NaviDrawer;
import com.mindware.workflow.ui.ui.components.navigation.drawer.NaviItem;
import com.mindware.workflow.ui.ui.components.navigation.drawer.NaviMenu;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.util.css.Overflow;
import com.mindware.workflow.ui.ui.views.Home;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
@CssImport("./styles/misc/box-shadow-borders.css")
@CssImport(value = "./styles/styles.css", include = "lumo-badge")
@CssImport(value = "./styles/components/orgchart.css")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
@PWA(name = "Workflow-PROMOCRED", shortName = "Workflow-PROMOCRED", iconPath = "images/logo.png", backgroundColor = "#233348", themeColor = "#233348")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Route("main")
public class MainLayout extends FlexBoxLayout
		implements RouterLayout, PageConfigurator, AfterNavigationObserver {

	private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
	private static final String CLASS_NAME = "root";

	private Div appHeaderOuter;

	private FlexBoxLayout row;
	private NaviDrawer naviDrawer;
	private FlexBoxLayout column;

	private Div appHeaderInner;
	private FlexBoxLayout viewContainer;
	private Div appFooterInner;

	private Div appFooterOuter;

	private TabBar tabBar;
	private boolean navigationTabs = false;
	private AppBar appBar;

	public List<Option> optionList = new ArrayList<>();

	public MainLayout() {
		VaadinSession.getCurrent()
				.setErrorHandler((ErrorHandler) errorEvent -> {
					log.error("Uncaught UI exception",
							errorEvent.getThrowable());
					Notification.show(
							"We are sorry, but an internal error occurred");
				});

		addClassName(CLASS_NAME);
		setFlexDirection(FlexDirection.COLUMN);
		setSizeFull();

		listOptions();

		// Initialise the UI building blocks
		initStructure();

		// Populate the navigation drawer
		initNaviItems();

		// Configure the headers and footers (optional)
		initHeadersAndFooters();
	}


	private void listOptions(){

		UserRestTemplate userRestTemplate = new UserRestTemplate();
		String login = VaadinSession.getCurrent().getAttribute("login").toString();
		Users users = userRestTemplate.getByIdUser(login);
		RolRestTemplate rolRestTemplate = new RolRestTemplate();
		Rol rol = rolRestTemplate.getRolByName(users.getRol());
		OfficeRestTemplate officeRestTemplate = new OfficeRestTemplate();
		Office office = officeRestTemplate.getByCode(users.getCodeOffice());

		String options = rol.getOptions();
		VaadinSession.getCurrent().setAttribute("options", options);
		VaadinSession.getCurrent().setAttribute("rol", users.getRol());
		VaadinSession.getCurrent().setAttribute("idOffice",users.getCodeOffice());
		VaadinSession.getCurrent().setAttribute("email",users.getEmail());
		VaadinSession.getCurrent().setAttribute("scope-rol",rol.getScope());
		VaadinSession.getCurrent().setAttribute("city",office.getCity());
		VaadinSession.getCurrent().setAttribute("name-user",users.getNames());
		VaadinSession.getCurrent().setAttribute("scope-user",users.getScope());
		VaadinSession.getCurrent().setAttribute("is-supervisor",users.getSupervisor());
	}

	private boolean assignedOption(String name){
		if(optionList.size()==0) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				optionList = mapper.readValue(VaadinSession.getCurrent().getAttribute("options").toString(),
						new TypeReference<List<Option>>() {});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		List<Option> options = optionList.stream().filter(value -> value.getName().equals(name))
				.collect(Collectors.toList());
		return options.get(0).isAssigned();
	}

	/**
	 * Initialise the required components and containers.
	 */
	private void initStructure() {
		naviDrawer = new NaviDrawer();

		viewContainer = new FlexBoxLayout();
		viewContainer.addClassName(CLASS_NAME + "__view-container");
		viewContainer.setOverflow(Overflow.HIDDEN);

		column = new FlexBoxLayout(viewContainer);
		column.addClassName(CLASS_NAME + "__column");
		column.setFlexDirection(FlexDirection.COLUMN);
		column.setFlexGrow(1, viewContainer);
		column.setOverflow(Overflow.HIDDEN);

		row = new FlexBoxLayout(naviDrawer, column);
		row.addClassName(CLASS_NAME + "__row");
		row.setFlexGrow(1, column);
		row.setOverflow(Overflow.HIDDEN);
		add(row);
		setFlexGrow(1, row);
	}

	/**
	 * Initialise the navigation items.
	 */
	private void initNaviItems() {
		NaviMenu menu = naviDrawer.getMenu();
//		menu.addNaviItem(VaadinIcon.HOME, "Home", Home.class);
//		menu.addNaviItem(VaadinIcon.INSTITUTION, "Accounts", Accounts.class);
//		menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Payments", Payments.class);
//		menu.addNaviItem(VaadinIcon.CHART, "Statistics", Statistics.class);
//
//		NaviItem personnel = menu.addNaviItem(VaadinIcon.USERS, "Personnel",
//				null);
//		menu.addNaviItem(personnel, "Accountants", Accountants.class);
//		menu.addNaviItem(personnel, "Managers", Managers.class);

		if(assignedOption("Clientes")){
			NaviItem mercadeo = menu.addNaviItem(VaadinIcon.SITEMAP,"Mercadeo",null);
			if(assignedOption("Clientes")){
				menu.addNaviItem(mercadeo, "Clientes", ClientView.class);
			}
//			if(assignedOption("Propuestas")){
//				menu.addNaviItem(mercadeo,"Propuestas",Home.class);
//			}
//			if(assignedOption("Monitoreo")){
//				menu.addNaviItem(mercadeo,"Monitoreo",Home.class);
//			}
		}

		if(assignedOption("Solicitantes")) {
			menu.addNaviItem(VaadinIcon.GROUP, "Solicitantes", ApplicantView.class);
		}
		if(assignedOption("Solicitud")) {
			menu.addNaviItem(VaadinIcon.CALC_BOOK, "Solicitud", CreditRequestView.class);
		}
		if(assignedOption("Declaracion Patrimonial")) {
			menu.addNaviItem(VaadinIcon.COIN_PILES, "Declaracion Patrimonial", CreditPatrimonialStatement.class);
		}
		if(assignedOption("Flujo de Caja")) {
			menu.addNaviItem(VaadinIcon.MONEY, "Flujo de Caja", CashFlowView.class);
		}

		if(assignedOption("Resolucion de Credito")) {
			menu.addNaviItem(VaadinIcon.FILE_PRESENTATION, "Resolucion de Credito", CreditResolutionCreditRequestDtoView.class);
		}
		if(assignedOption("Formulario Observaciones")) {
			menu.addNaviItem(VaadinIcon.NEWSPAPER, "Formulario Observaciones", ObservationCreditRequestApplicantView.class);
		}


		if(assignedOption("Informe Legal")
				|| assignedOption("Contratos") || assignedOption("Variables de Contratos")) {
			NaviItem legal = menu.addNaviItem(VaadinIcon.BRIEFCASE, "Asesoria Legal", null);
			if (assignedOption("Informe Legal")) {
				menu.addNaviItem(legal, "Informe Legal", LegalInformationView.class);
			}
			if (assignedOption("Contratos")){
				menu.addNaviItem(legal, "Contratos", ContractCreditRequestDtoView.class);
			}
			if(assignedOption("Plantilla Contratos")){
				menu.addNaviItem(legal,"Plantilla Contratos", TemplateContractView.class);
			}
			if(assignedOption("Variables de Contratos")){
				menu.addNaviItem(legal,"Variables de Contratos", ContractVariableView.class);
			}
		}
		if(assignedOption("Bandeja Seguimiento")) {
			menu.addNaviItem(VaadinIcon.OUTBOX, "Bandeja Seguimiento", StageHistoryGlobalView.class);
		}
		if(assignedOption("Bandeja Pendientes")) {
			menu.addNaviItem(VaadinIcon.INBOX, "Bandeja Pendientes", StageHistoryView.class);
		}
		if(assignedOption("Autorizar Excepciones")){
			menu.addNaviItem(VaadinIcon.SPECIALIST, "Autorizar Excepciones", AuthorizerExceptionsCreditRequestDtoView.class);
		}

		if(assignedOption("Oficinas") || assignedOption("Responsables")
				|| assignedOption("Parametros") || assignedOption("Plantillas")
				|| assignedOption("Plantilla Observaciones") || assignedOption("Roles")
				|| assignedOption("Usuarios") || assignedOption("Flujo por Producto")) {

			NaviItem configuration = menu.addNaviItem(VaadinIcon.COGS, "Configuracion", null);
			if (assignedOption("Oficinas")) {
				menu.addNaviItem(configuration, "Oficinas", OfficeView.class);
			}
			if (assignedOption("Responsables")) {
				menu.addNaviItem(configuration, "Responsables", SignatorieView.class);
			}
			if (assignedOption("Parametros")) {
				menu.addNaviItem(configuration, "Parametros", ParameterView.class);
			}
			if (assignedOption("Plantillas")) {
				menu.addNaviItem(configuration, "Plantillas", TemplateFormsView.class);
			}
			if (assignedOption("Plantilla Observaciones")) {
				menu.addNaviItem(configuration, "Plantilla Observaciones", TemplateObservationView.class);
			}
			if(assignedOption("Flujo por Producto")){
				menu.addNaviItem(configuration,"Flujo por Producto", WorflowProductView.class);
			}
			if (assignedOption("Roles")) {
				menu.addNaviItem(configuration, "Roles", RolView.class);
			}
			if (assignedOption("Usuarios")) {
				menu.addNaviItem(configuration, "Usuarios", UsersView.class);
			}
			if(assignedOption("Autorizadores")){
				menu.addNaviItem(configuration,"Autorizadores", UserAuthorizerView.class);
			}

			if(assignedOption("Excepciones")){
				menu.addNaviItem(configuration, "Excepciones", ExceptionsView.class);
			}

			if(assignedOption("Tipo de Cambio")){
				menu.addNaviItem(configuration,"Tipo de Cambio", ExchangeRateView.class);
			}

			if(assignedOption("Ciudad-Provincias")){
				menu.addNaviItem(configuration,"Ciudad-Provincias", CityProvinceView.class);
			}
		}
	}

	/**
	 * Configure the app's inner and outer headers and footers.
	 */
	private void initHeadersAndFooters() {
		// setAppHeaderOuter();
		// setAppFooterInner();
		// setAppFooterOuter();

		// Default inner header setup:
		// - When using tabbed navigation the view title, user avatar and main menu button will appear in the TabBar.
		// - When tabbed navigation is turned off they appear in the AppBar.

		appBar = new AppBar("");

		// Tabbed navigation
		if (navigationTabs) {
			tabBar = new TabBar();
			UIUtils.setTheme(Lumo.DARK, tabBar);

			// Shift-click to add a new tab
			for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
				item.addClickListener(e -> {
					if (e.getButton() == 0 && e.isShiftKey()) {
						tabBar.setSelectedTab(tabBar.addClosableTab(item.getText(), item.getNavigationTarget()));
					}
				});
			}
			appBar.getAvatar().setVisible(false);
			setAppHeaderInner(tabBar, appBar);

			// Default navigation
		} else {
			UIUtils.setTheme(Lumo.DARK, appBar);
			setAppHeaderInner(appBar);
		}
	}

	private void setAppHeaderOuter(Component... components) {
		if (appHeaderOuter == null) {
			appHeaderOuter = new Div();
			appHeaderOuter.addClassName("app-header-outer");
			getElement().insertChild(0, appHeaderOuter.getElement());
		}
		appHeaderOuter.removeAll();
		appHeaderOuter.add(components);
	}

	private void setAppHeaderInner(Component... components) {
		if (appHeaderInner == null) {
			appHeaderInner = new Div();
			appHeaderInner.addClassName("app-header-inner");
			column.getElement().insertChild(0, appHeaderInner.getElement());
		}
		appHeaderInner.removeAll();
		appHeaderInner.add(components);
	}

	private void setAppFooterInner(Component... components) {
		if (appFooterInner == null) {
			appFooterInner = new Div();
			appFooterInner.addClassName("app-footer-inner");
			column.getElement().insertChild(column.getElement().getChildCount(),
					appFooterInner.getElement());
		}
		appFooterInner.removeAll();
		appFooterInner.add(components);
	}

	private void setAppFooterOuter(Component... components) {
		if (appFooterOuter == null) {
			appFooterOuter = new Div();
			appFooterOuter.addClassName("app-footer-outer");
			getElement().insertChild(getElement().getChildCount(),
					appFooterOuter.getElement());
		}
		appFooterOuter.removeAll();
		appFooterOuter.add(components);
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addMetaTag("apple-mobile-web-app-capable", "yes");
		settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");

		settings.addFavIcon("icon", "static/frontend/images/favicons/favicon.ico",
				"256x256");
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		this.viewContainer.getElement().appendChild(content.getElement());
	}

	public NaviDrawer getNaviDrawer() {
		return naviDrawer;
	}

	public static MainLayout get() {
		return (MainLayout) UI.getCurrent().getChildren()
				.filter(component -> component.getClass() == MainLayout.class)
				.findFirst().get();
	}

	public AppBar getAppBar() {
		return appBar;
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if (navigationTabs) {
			afterNavigationWithTabs(event);
		} else {
			afterNavigationWithoutTabs(event);
		}
	}

	private void afterNavigationWithTabs(AfterNavigationEvent e) {
		NaviItem active = getActiveItem(e);
		if (active == null) {
			if (tabBar.getTabCount() == 0) {
				tabBar.addClosableTab("", Home.class);
			}
		} else {
			if (tabBar.getTabCount() > 0) {
				tabBar.updateSelectedTab(active.getText(),
						active.getNavigationTarget());
			} else {
				tabBar.addClosableTab(active.getText(),
						active.getNavigationTarget());
			}
		}
		appBar.getMenuIcon().setVisible(false);
	}

	private NaviItem getActiveItem(AfterNavigationEvent e) {
		for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
			if (item.isHighlighted(e)) {
				return item;
			}
		}
		return null;
	}

	private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
		NaviItem active = getActiveItem(e);
		if (active != null) {
			getAppBar().setTitle(active.getText());
		}
	}

}
