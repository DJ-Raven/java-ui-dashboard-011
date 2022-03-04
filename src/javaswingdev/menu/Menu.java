package javaswingdev.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javaswingdev.GoogleMaterialDesignIcon;
import javaswingdev.swing.scroll.ScrollBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class Menu extends JPanel {

    private int index = -1;
    private final List<EventMenuSelected> events = new ArrayList<>();

    public Menu() {
        init();
    }

    private void init() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        JScrollPane scroll = createScroll();
        panelMenu = createPanelMenu();
        scroll.setViewportView(panelMenu);
        scroll.getViewport().setOpaque(false);
        scroll.setViewportBorder(null);
        add(scroll);
        addTitle("MAIN");
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.DASHBOARD, "Dashboard"));
        addTitle("WEB APPS");
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.MAIL_OUTLINE, "Email", "Inbox", "Read", "Compose"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.MESSAGE, "Chat"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.PERM_CONTACT_CALENDAR, "Calendar"));
        addTitle("COMPONENT");
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.WHATSHOT, "UI Kit", "Accordion", "Alerts", "Badges", "Breadcrumbs", "Buttons", "Button group"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.DIRECTIONS_BIKE, "Advanced UI", "Cropper", "Owl Carousel", "Sweet Alert"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.DVR, "Forms", "Basic Elements", "Advanced Elements", "SEditors", "Wizard"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.PIE_CHART_OUTLINED, "Charts", "Apex", "Flot", "Peity", "Sparkline"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.VIEW_LIST, "Table", "Basic Tables", "Data Table"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.INSERT_EMOTICON, "Icons", "Feather Icons", "Flag Icons", "Mdi Icons"));
        addTitle("PAGES");
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.INBOX, "Special Pages", "Blank page", "Faq", "Invoice", "Profile", "Pricing", "Timeline"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.LOCK_OUTLINE, "Authentication", "Login", "Register"));
        addMenuItem(new ModelMenuItem(GoogleMaterialDesignIcon.ERROR_OUTLINE, "Error", "404", "500"));
    }

    private JScrollPane createScroll() {
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBar(new ScrollBar());
        return scroll;
    }

    private JPanel createPanelMenu() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        menuLayout = new MigLayout("wrap,fillx,inset 0,gapy 0", "[fill]");
        panel.setLayout(menuLayout);

        return panel;
    }

    private JPanel createMenuItem(ModelMenuItem item) {
        MenuItem menuItem = new MenuItem(item, ++index, menuLayout);
        menuItem.addEvent(new EventMenuSelected() {
            @Override
            public void menuSelected(int index, int indexSubMenu) {
                if (!menuItem.isHasSubMenu() || indexSubMenu != 0) {
                    clearSelected();
                    setSelectedIndex(index, indexSubMenu);
                }
            }
        });
        return menuItem;
    }

    private void runEvent(int index, int indexSubMenu) {
        for (EventMenuSelected event : events) {
            event.menuSelected(index, indexSubMenu);
        }
    }

    //  Public Method
    public void addMenuItem(ModelMenuItem menu) {
        panelMenu.add(createMenuItem(menu), "h 35!");
    }

    public void addTitle(String title) {
        JLabel label = new JLabel(title);
        label.setBorder(new EmptyBorder(15, 20, 5, 5));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setForeground(new Color(170, 170, 170));
        panelMenu.add(label);
    }

    public void addSpace(int size) {
        panelMenu.add(new JLabel(), "h " + size + "!");
    }

    public void setSelectedIndex(int index, int indexSubMenu) {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getIndex() == index) {
                    item.setSelectedIndex(indexSubMenu);
                    runEvent(index, indexSubMenu);
                    break;
                }
            }
        }
    }

    public void clearSelected() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                item.clearSelected();
            }
        }
    }

    public void addEvent(EventMenuSelected event) {
        events.add(event);
    }

    private MigLayout menuLayout;
    private JPanel panelMenu;
}
