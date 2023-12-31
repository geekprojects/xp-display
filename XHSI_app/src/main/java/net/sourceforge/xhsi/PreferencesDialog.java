/**
 * PreferencesDialog.java
 * 
 * Dialog for setting preferences.
 * 
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2010-2015  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2015-2019  Nicolas Carel
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sourceforge.xhsi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sourceforge.xhsi.flightdeck.empty.EmptyComponent;
import net.sourceforge.xhsi.flightdeck.pfd.PFDComponent;
import net.sourceforge.xhsi.flightdeck.nd.NDComponent;
import net.sourceforge.xhsi.flightdeck.eicas.EICASComponent;
import net.sourceforge.xhsi.flightdeck.mfd.MFDComponent;
import net.sourceforge.xhsi.flightdeck.annunciators.AnnunComponent;
import net.sourceforge.xhsi.flightdeck.clock.ClockComponent;
import net.sourceforge.xhsi.flightdeck.cdu.CDUComponent;


public class PreferencesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private XHSIPreferences preferences;

    /*
     * System tab
     * ----------
     */ 
    //private JComboBox simcom_combobox;
    //private String[] simcoms = { XHSIPreferences.XHSI_PLUGIN, XHSIPreferences.SCS };
    
    private JTextField aptnav_dir_textfield;
    private JTextField egpws_db_dir_textfield;
    private JTextField port_textfield;
    private JTextField weather_port_textfield;
    private JTextField group_textfield;
    private JCheckBox multicast_checkbox;
    private Level[] loglevels = { Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST };
    private JComboBox<Level> loglevel_combobox;
    private JComboBox<String> operator_combobox;
    private String operators[] = { XHSIPreferences.PILOT, XHSIPreferences.COPILOT, XHSIPreferences.INSTRUCTOR };
    private JCheckBox allow_shutdown;

    /*
     * Windows tab
     * -----------
     */    
    private JCheckBox start_ontop_checkbox;
    private JCheckBox hide_window_frame_checkbox;
    private JCheckBox panel_locked_checkbox;
    private JButton get_button;
    private static final int MAX_WINS = 9; // Empty, PFD, ND, EICAS, MFD, Annunciators, Clock and CDU
    private JCheckBox panel_active_checkbox[] = new JCheckBox[MAX_WINS];
    private JTextField panel_pos_x_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_pos_y_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_width_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_height_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_border_textfield[] = new JTextField[MAX_WINS];
    private JCheckBox panel_square_checkbox[] = new JCheckBox[MAX_WINS];
    @SuppressWarnings("unchecked")
	private JComboBox<String> panel_orientation_combobox[] = new JComboBox[MAX_WINS];
    private String orientations[] = {
            XHSIPreferences.Orientation.UP.get_rotation(),
            XHSIPreferences.Orientation.LEFT.get_rotation(),
            XHSIPreferences.Orientation.RIGHT.get_rotation(),
            XHSIPreferences.Orientation.DOWN.get_rotation(),
            };
    private int du_pos_x[] = new int[MAX_WINS];
    private int du_pos_y[] = new int[MAX_WINS];
    private int du_width[] = new int[MAX_WINS];
    private int du_height[] = new int[MAX_WINS];
    private JCheckBox conwin_minimized_checkbox;
    
    /*
     * Graphics tab
     * ------------
     */    
    private JComboBox<String> border_style_combobox;
    private String borderstyles[] = { XHSIPreferences.BORDER_RELIEF, XHSIPreferences.BORDER_LIGHT, XHSIPreferences.BORDER_DARK, XHSIPreferences.BORDER_NONE };
    private JComboBox<String> border_color_combobox;
    private String bordercolors[] = { XHSIPreferences.BORDER_GRAY, XHSIPreferences.BORDER_BROWN, XHSIPreferences.BORDER_BLUE };
    private JCheckBox use_more_color_checkbox;
    private JComboBox<String> instruments_font_combobox;
    private String instruments_fonts[] = { "Builtin", "Verdana", "Tahoma", "boeingGlass", "Arial", "Arial Rounded MT Bold", "DejaVu Sans Mono", "FreeSans", "Lucida Sans", "MS Reference Sans Serif", "Ubuntu", "Ubuntu Mono", "Lucida Sans" };
    private JComboBox<String> cdu_font_combobox;
    private String cdu_fonts[] = { "Builtin", "Andale Mono", "DejaVu Sans Mono", "Ubuntu Mono" };

    // TODO: Get system fonts list
    private JCheckBox bold_fonts_checkbox;    
    private JCheckBox anti_alias_checkbox;
    private JCheckBox draw_bezier_pavements_checkbox;

    /*
     * Avionics tab
     * ------------
     */   
    private JComboBox<String> instrument_style_combobox;
    private JCheckBox digital_clock_checkbox;
    private String instrument_styles[] = { XHSIPreferences.INSTRUMENT_STYLE_SWITCHABLE, XHSIPreferences.INSTRUMENT_STYLE_BOEING, XHSIPreferences.INSTRUMENT_STYLE_AIRBUS };
    private JTextField min_rwy_textfield;
    private JComboBox<String> rwy_units_combobox;
    private String units[] = { "meters", "feet" };
    private JCheckBox use_power_checkbox;
    private JCheckBox auto_frontcourse_checkbox;
    private JComboBox<String> hsi_source_combobox;
    private String hsi_sources[] = { XHSIPreferences.USER, XHSIPreferences.NAV1, XHSIPreferences.NAV2 };
    
    /*
     * PFD tab
     * -------
     */
    private JComboBox<String> horizon_style_combobox;
    private String horizons[] = { XHSIPreferences.HORIZON_SQUARE, XHSIPreferences.HORIZON_ROUNDED, XHSIPreferences.HORIZON_FULLWIDTH, XHSIPreferences.HORIZON_FULLSCREEN /*, XHSIPreferences.HORIZON_AIRBUS */ };
    private JComboBox<String> dial_transparency_combobox;
    private String transparencies[] = { "0", "25", "50", "75" };
    private JCheckBox draw_single_cue_fd_checkbox;
    private JCheckBox draw_aoa_checkbox;
    private JCheckBox pfd_hsi_checkbox;
    private JCheckBox colored_hsi_course_checkbox;
    private JCheckBox draw_radios_checkbox;
    private JCheckBox adi_centered_checkbox;
    private JCheckBox draw_twinspeeds_checkbox;
    private JCheckBox draw_turnrate_checkbox;
    private JCheckBox draw_gmeter_checkbox;
    private JComboBox<String> draw_yoke_input_combobox;    
    private String draw_yoke_input[] = { XHSIPreferences.YOKE_INPUT_NONE, XHSIPreferences.YOKE_INPUT_AUTO, XHSIPreferences.YOKE_INPUT_RUDDER, XHSIPreferences.YOKE_INPUT_ALWAYS, XHSIPreferences.YOKE_INPUT_ALWAYS_RUDDER };
    private JComboBox<String> speed_unit_combobox;
    private String speed_unit_input[] = { XHSIPreferences.PFD_SPEED_KTS, XHSIPreferences.PFD_SPEED_KMH, XHSIPreferences.PFD_SPEED_KTS_KMH };

    /*
     * ND tab
     * ------
     */
    private JCheckBox airbus_modes_checkbox;
    private JCheckBox symbols_multiselection_checkbox; 
    private JCheckBox classic_hsi_checkbox;
    private JCheckBox appvor_uncluttered_checkbox;
    private JCheckBox mode_mismatch_caution_checkbox;    
    private JCheckBox plan_aircraft_center_checkbox;
    private JCheckBox draw_inside_rose_checkbox;   
    private JCheckBox draw_range_arcs_checkbox;
    private JCheckBox limit_arcs_60deg_checkbox;
    private JComboBox<String> limit_arcs_deg_combobox;
    private String[] arc_limits = { "0","60","70","75","80","85","90" };
    private JCheckBox draw_rwy_checkbox;
    private JCheckBox tcas_always_on_checkbox;
    private JCheckBox nd_navaid_frequencies;
    private JCheckBox nd_write_ap_hdg;
    private JCheckBox nd_show_clock;
    private JCheckBox nd_show_helipads;
    
    /*
     * EGPWS tab
     * ---------
     */
    private JComboBox<String> terrain_resolution_combobox;
    private String terrain_resolutions[] = { XHSIPreferences.RES_FINE, XHSIPreferences.RES_MEDIUM, XHSIPreferences.RES_COARSE };
    private JCheckBox nd_show_vertical_path;
    private JCheckBox nd_terrain_auto_display;
    private JCheckBox nd_terrain_peaks_mode;
    private JCheckBox nd_terrain_sweep;
    private JCheckBox nd_terrain_sweep_bar;
    private JTextField nd_terrain_sweep_time;

    /*
     * Weather Radar tab
     * -----------------
     */
    private JComboBox<String> wxr_resolution_combobox;
    private String wxr_resolutions[] = { XHSIPreferences.RES_FINE, XHSIPreferences.RES_MEDIUM, XHSIPreferences.RES_COARSE };
    private JCheckBox nd_wxr_sweep;
    private JCheckBox nd_wxr_sweep_bar;
    private JTextField nd_wxr_sweep_time;
    private JCheckBox nd_wxr_dual_settings;
    private JCheckBox nd_wxr_color_gradient;
    // realistic attenuation    
 
    /*
     * EICAS tab
     * ---------
     */
    private JComboBox<String> eicas_layout_combobox;
    private final String[] eicas_layouts = { XHSIPreferences.EICAS_LAYOUT_PRIMARY, XHSIPreferences.EICAS_LAYOUT_PRIMARY_AND_CONTROLS, XHSIPreferences.EICAS_LAYOUT_FULL };
    private JComboBox<String> engine_count_combobox;
    private JComboBox<String> engine_type_combobox;
    private String engine_types[] = { XHSIPreferences.ENGINE_TYPE_SWITCHABLE, XHSIPreferences.ENGINE_TYPE_N1, XHSIPreferences.ENGINE_TYPE_EPR, XHSIPreferences.ENGINE_TYPE_TRQ, XHSIPreferences.ENGINE_TYPE_MAP };
    private JComboBox<String> trq_scale_combobox;
    private String trq_scales[] = { XHSIPreferences.TRQ_SCALE_SWITCHABLE, XHSIPreferences.TRQ_SCALE_LBFT, XHSIPreferences.TRQ_SCALE_NM, XHSIPreferences.TRQ_SCALE_PERCENT };;
    private JComboBox<String> fuel_unit_combobox;
    private String fuel_units[] = { XHSIPreferences.FUEL_UNITS_SWITCHABLE, XHSIPreferences.FUEL_UNITS_KG, XHSIPreferences.FUEL_UNITS_LBS, XHSIPreferences.FUEL_UNITS_USG, XHSIPreferences.FUEL_UNITS_LTR };
    private JComboBox<String> temp_unit_combobox;
    private String temp_units[] = { XHSIPreferences.TEMP_UNITS_SWITCHABLE, XHSIPreferences.TEMP_UNITS_CELCIUS, XHSIPreferences.TEMP_UNITS_FAHRENHEIT };

    /*
     * MFD tab
     * -------
     */
    private JComboBox<String> mfd_mode_combobox;
    private String mfd_modes[] = { 
    		XHSIPreferences.MFD_MODE_SWITCHABLE,
    		XHSIPreferences.MFD_MODE_LINKED,    		
    		XHSIPreferences.MFD_MODE_ARPT_CHART,
    		XHSIPreferences.MFD_MODE_FPLN,
    		XHSIPreferences.MFD_MODE_RTU,
    		XHSIPreferences.MFD_MODE_LOWER_EICAS,
    		XHSIPreferences.MFD_MODE_BLEED,
    		XHSIPreferences.MFD_MODE_CAB_PRESS,
    		XHSIPreferences.MFD_MODE_ELEC,
    		XHSIPreferences.MFD_MODE_HYDR,
    		XHSIPreferences.MFD_MODE_FUEL,
    		XHSIPreferences.MFD_MODE_APU,
    		XHSIPreferences.MFD_MODE_COND,
    		XHSIPreferences.MFD_MODE_DOOR_OXY,
    		XHSIPreferences.MFD_MODE_WHEELS,
    		XHSIPreferences.MFD_MODE_FCTL,
            XHSIPreferences.MFD_MODE_SYS,
    		XHSIPreferences.MFD_MODE_STATUS };    
    private JComboBox<String> arpt_chart_color_combobox;
    private String arpt_chart_colors[] = { XHSIPreferences.ARPT_DIAGRAM_COLOR_AUTO, XHSIPreferences.ARPT_DIAGRAM_COLOR_DAY, XHSIPreferences.ARPT_DIAGRAM_COLOR_NIGHT };
    private JCheckBox arpt_chart_nav_dest;

    /*
     * CDU tab
     * -------
     */
    private JCheckBox cdu_display_only;
    private JComboBox<String> cdu_source_combobox;
    private String cdu_sources[] = { XHSIPreferences.CDU_SOURCE_SWITCHABLE, XHSIPreferences.CDU_SOURCE_AIRCRAFT_OR_DUMMY, XHSIPreferences.CDU_SOURCE_XFMC, XHSIPreferences.CDU_SOURCE_UFMC };
    private JComboBox<String> cdu_side_combobox;
    private String cdu_sides[] = { XHSIPreferences.CDU_SIDE_SWITCHABLE, XHSIPreferences.CDU_SIDE_LEFT, XHSIPreferences.CDU_SIDE_RIGHT };


    private ArrayList<XHSIInstrument> flightdeck;

    private String field_validation_errors = null;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public PreferencesDialog(JFrame owner_frame, ArrayList<XHSIInstrument> fd) {

        super(owner_frame, "XHSI Preferences");

        this.flightdeck = fd;

        this.preferences = XHSIPreferences.get_instance();

        this.setResizable(false);
        
        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_preferences_tabs(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_preferences();
        enable_lock_fields();
        pack();

    }


    private void init_preferences() {

        // SYSTEM

        String instrument_position = preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION);
        for (int i=0; i<operators.length; i++) {
            if ( instrument_position.equalsIgnoreCase(operators[i]) ) {
                this.operator_combobox.setSelectedIndex(i);
            }
        }

//        for (int i=0; i<simcoms.length; i++) {
//            if ( preferences.get_preference(XHSIPreferences.PREF_SIMCOM).equals(simcoms[i]) ) {
//                this.simcom_combobox.setSelectedIndex(i);
//            }
//        }

        this.aptnav_dir_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
        this.egpws_db_dir_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_EGPWS_DB_DIR));
        this.port_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PORT));
        this.weather_port_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_WEATHER_PORT));
        this.group_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_GROUP));
        this.multicast_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_MULTICAST).equalsIgnoreCase("true"));;

        for (int i=0; i<loglevels.length; i++) {
            if (logger.getLevel() == loglevels[i]) {
                this.loglevel_combobox.setSelectedIndex(i);
            }
        }
        this.allow_shutdown.setSelected(preferences.get_preference(XHSIPreferences.PREF_ALLOW_SHUTDOWN).equalsIgnoreCase("true"));;

        
        // GRAPHICS
        
        // Get system font list
        /*
        GraphicsEnvironment ge;  
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        String[] font_families = ge.getAvailableFontFamilyNames();
        */

        String instrumentstyle = preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE);
        for (int i=0; i<instrument_styles.length; i++) {
            if ( instrumentstyle.equals( instrument_styles[i] ) ) {
                this.instrument_style_combobox.setSelectedIndex(i);
            }
        }
        
        String instruments_font = preferences.get_preference(XHSIPreferences.PREF_INSTRUMENTS_FONT);
        for (int i=0; i<instruments_fonts.length; i++) {
            if ( instruments_font.equals( instruments_fonts[i] ) ) {
                this.instruments_font_combobox.setSelectedIndex(i);
            }
        }
        
        String cdu_font = preferences.get_preference(XHSIPreferences.PREF_CDU_FONT);
        for (int i=0; i<instruments_fonts.length; i++) {
            if ( cdu_font.equals( instruments_fonts[i] ) ) {
                this.cdu_font_combobox.setSelectedIndex(i);
            }
        }
        
        this.bold_fonts_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equalsIgnoreCase("true"));

        this.use_more_color_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equalsIgnoreCase("true"));

        this.anti_alias_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equalsIgnoreCase("true"));

        String border_style = preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE);
        for (int i=0; i<borderstyles.length; i++) {
            if ( border_style.equals( borderstyles[i] ) ) {
                this.border_style_combobox.setSelectedIndex(i);
            }
        }

        String border_color = preferences.get_preference(XHSIPreferences.PREF_BORDER_COLOR);
        for (int i=0; i<bordercolors.length; i++) {
            if ( border_color.equals( bordercolors[i] ) ) {
                this.border_color_combobox.setSelectedIndex(i);
            }
        }

        this.draw_bezier_pavements_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS).equalsIgnoreCase("true"));


        // WINDOWS

        this.start_ontop_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equalsIgnoreCase("true"));

        this.hide_window_frame_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES).equalsIgnoreCase("true"));

        this.panel_locked_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PANELS_LOCKED).equalsIgnoreCase("true"));

        for (int j = 0; j<MAX_WINS; j++) {

            this.panel_active_checkbox[j].setSelected( preferences.get_panel_active(j) );

            this.panel_pos_x_textfield[j].setText( "" + preferences.get_panel_pos_x(j) );
            this.panel_pos_y_textfield[j].setText( "" + preferences.get_panel_pos_y(j) );
            this.panel_width_textfield[j].setText( "" + preferences.get_panel_width(j) );
            this.panel_height_textfield[j].setText( "" + preferences.get_panel_height(j) );
            this.panel_border_textfield[j].setText( "" + preferences.get_panel_border(j) );

            this.panel_square_checkbox[j].setSelected( preferences.get_panel_square(j) );

            XHSIPreferences.Orientation pref_orientation = preferences.get_panel_orientation(j);
            for (int i=0; i<orientations.length; i++) {
                if ( pref_orientation.get_rotation().equals( orientations[i] ) ) {
                    this.panel_orientation_combobox[j].setSelectedIndex(i);
                }
            }

        }

        this.conwin_minimized_checkbox.setSelected(preferences.get_conwin_minimized());


        // Avionics Options
      
        this.digital_clock_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_CLOCK_DISPLAY_DIGITAL).equalsIgnoreCase("true"));
        
        this.use_power_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_POWER).equalsIgnoreCase("true"));

        this.auto_frontcourse_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE).equalsIgnoreCase("true"));

        this.hsi_source_combobox.setSelectedIndex( preferences.get_hsi_source() );


        // ND Options (12)

        this.min_rwy_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN));

        String rwy_units = preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS);
        for (int i=0; i<units.length; i++) {
            if ( rwy_units.equals( units[i] ) ) {
                this.rwy_units_combobox.setSelectedIndex(i);
            }
        }

        this.airbus_modes_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equalsIgnoreCase("true"));
        
        this.symbols_multiselection_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_SYMBOLS_MULTISELECTION).equalsIgnoreCase("true"));

        this.draw_rwy_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equalsIgnoreCase("true"));

        this.draw_range_arcs_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equalsIgnoreCase("true"));

        this.limit_arcs_60deg_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_LIMIT_ARCS_60DEG).equalsIgnoreCase("true"));
     
        String limits_arcs_deg = preferences.get_preference(XHSIPreferences.PREF_LIMIT_ARCS_DEG);
        for (int i=0; i<arc_limits.length; i++) {
            if ( limits_arcs_deg.equals( arc_limits[i] ) ) {
                this.limit_arcs_deg_combobox.setSelectedIndex(i);
            }
        }
        
        this.mode_mismatch_caution_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equalsIgnoreCase("true"));

        this.tcas_always_on_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equalsIgnoreCase("true"));

        this.classic_hsi_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equalsIgnoreCase("true"));

        this.appvor_uncluttered_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equalsIgnoreCase("true"));

        this.plan_aircraft_center_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equalsIgnoreCase("true"));

        this.draw_inside_rose_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE).equalsIgnoreCase("true"));

        this.colored_hsi_course_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE).equalsIgnoreCase("true"));

        this.nd_navaid_frequencies.setSelected(preferences.get_preference(XHSIPreferences.PREF_ND_NAVAID_FREQ).equalsIgnoreCase("true"));

        this.nd_write_ap_hdg.setSelected(preferences.get_preference(XHSIPreferences.PREF_ND_WRITE_AP_HDG).equalsIgnoreCase("true"));

        this.nd_show_clock.setSelected(preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_CLOCK).equalsIgnoreCase("true"));
        
        this.nd_show_helipads.setSelected(preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_HELIPADS).equalsIgnoreCase("true"));

        // EGPWS Terrain options
        this.nd_terrain_sweep.setSelected(preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP).equalsIgnoreCase("true"));
        this.nd_terrain_sweep_bar.setSelected(preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_BAR).equalsIgnoreCase("true"));
        this.nd_terrain_sweep_time.setText(preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_DURATION));
        String terrain_resolution = preferences.get_preference(XHSIPreferences.PREF_TERRAIN_RESOLUTION);
        for (int i=0; i<terrain_resolutions.length; i++) {
            if ( terrain_resolution.equals( terrain_resolutions[i] ) ) {
                this.terrain_resolution_combobox.setSelectedIndex(i);
            }
        }
        this.nd_show_vertical_path.setSelected(preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_VERTICAL_PATH).equalsIgnoreCase("true"));
        this.nd_terrain_auto_display.setSelected(preferences.get_preference(XHSIPreferences.PREF_TERRAIN_AUTO_DISPLAY).equalsIgnoreCase("true"));
        this.nd_terrain_peaks_mode.setSelected(preferences.get_preference(XHSIPreferences.PREF_TERRAIN_PEAKS_MODE).equalsIgnoreCase("true"));
        // TODO: PREF_EGPWS_INHIBIT

        // Weather radar options   
        String wxr_resolution = preferences.get_preference(XHSIPreferences.PREF_WXR_RESOLUTION);
        for (int i=0; i<wxr_resolutions.length; i++) {
            if ( wxr_resolution.equals( wxr_resolutions[i] ) ) {
                this.wxr_resolution_combobox.setSelectedIndex(i);
            }
        }
        this.nd_wxr_sweep.setSelected(preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP).equalsIgnoreCase("true"));
        this.nd_wxr_sweep_bar.setSelected(preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP_BAR).equalsIgnoreCase("true"));
        this.nd_wxr_sweep_time.setText(preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP_DURATION));

        
        // PFD Options (3)

        String horizonstyle = preferences.get_preference(XHSIPreferences.PREF_HORIZON_STYLE);
        for (int i=0; i<horizons.length; i++) {
            if ( horizonstyle.equals( horizons[i] ) ) {
                this.horizon_style_combobox.setSelectedIndex(i);
            }
        }

        String transp = preferences.get_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY);
        for (int i=0; i<transparencies.length; i++) {
            if ( transp.equals( transparencies[i] ) ) {
                this.dial_transparency_combobox.setSelectedIndex(i);
            }
        }

        this.draw_single_cue_fd_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_SINGLE_CUE_FD).equalsIgnoreCase("true"));

        this.draw_aoa_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_AOA).equalsIgnoreCase("true"));

        this.pfd_hsi_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_HSI).equalsIgnoreCase("true"));

        this.draw_radios_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS).equalsIgnoreCase("true"));

        this.adi_centered_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_ADI_CENTERED).equalsIgnoreCase("true"));

        this.draw_twinspeeds_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_TWINSPEEDS).equalsIgnoreCase("true"));

        this.draw_turnrate_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_TURNRATE).equalsIgnoreCase("true"));

        this.draw_gmeter_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_GMETER).equalsIgnoreCase("true"));

        String yoke_input = preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_YOKE_INPUT);
        for (int i=0; i<draw_yoke_input.length; i++) {
            if ( yoke_input.equals( draw_yoke_input[i] ) ) {
                this.draw_yoke_input_combobox.setSelectedIndex(i);
            }
        }

        // TODO: speed_unit_combobox

        // EICAS Options (5)

        String eicas_layout = preferences.get_preference(XHSIPreferences.PREF_EICAS_LAYOUT);
        for (int i=0; i<eicas_layouts.length; i++) {
            if ( eicas_layout.equals( eicas_layouts[i] ) ) {
                this.eicas_layout_combobox.setSelectedIndex(i);
            }
        }

        this.engine_count_combobox.setSelectedIndex( preferences.get_override_engine_count() );
        
        String engine = preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE);
        for (int i=0; i<engine_types.length; i++) {
            if ( engine.equals( engine_types[i] ) ) {
                this.engine_type_combobox.setSelectedIndex(i);
            }
        }

        String trq_scale = preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE);
        for (int i=0; i<trq_scales.length; i++) {
            if ( trq_scale.equals( trq_scales[i] ) ) {
                this.trq_scale_combobox.setSelectedIndex(i);
            }
        }

        String fuel = preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS);
        for (int i=0; i<fuel_units.length; i++) {
            if ( fuel.equals( fuel_units[i] ) ) {
                this.fuel_unit_combobox.setSelectedIndex(i);
            }
        }

        String temp = preferences.get_preference(XHSIPreferences.PREF_TEMP_UNITS);
        for (int i=0; i<temp_units.length; i++) {
            if ( temp.equals( temp_units[i] ) ) {
                this.temp_unit_combobox.setSelectedIndex(i);
            }
        }
        // MFD Options (3)

        String mfd_mode = preferences.get_preference(XHSIPreferences.PREF_MFD_MODE);
        for (int i=0; i<mfd_modes.length; i++) {
            if ( mfd_mode.equals( mfd_modes[i] ) ) {
                this.mfd_mode_combobox.setSelectedIndex(i);
            }
        }

        String taxichart = preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR);
        for (int i=0; i<arpt_chart_colors.length; i++) {
            if ( taxichart.equals( arpt_chart_colors[i] ) ) {
                this.arpt_chart_color_combobox.setSelectedIndex(i);
            }
        }

        this.arpt_chart_nav_dest.setSelected(preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_NAV_DEST).equalsIgnoreCase("true"));
        
        
        // CDU Options (2)
        
        this.cdu_display_only.setSelected(preferences.get_preference(XHSIPreferences.PREF_CDU_DISPLAY_ONLY).equalsIgnoreCase("true"));

        String cdu_source = preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE);
        for (int i=0; i<cdu_sources.length; i++) {
            if ( cdu_source.equals( cdu_sources[i] ) ) {
                this.cdu_source_combobox.setSelectedIndex(i);
            }
        }

        String cdu_side = preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE);
        for (int i=0; i<cdu_sides.length; i++) {
            if ( cdu_side.equals( cdu_sides[i] ) ) {
                this.cdu_side_combobox.setSelectedIndex(i);
            }
        }

    }


    private JTabbedPane create_preferences_tabs() {

        JTabbedPane tabs_panel = new JTabbedPane();
        tabs_panel.add( "System", create_system_tab() );
        tabs_panel.add( "Windows", create_windows_tab() );
        tabs_panel.add( "Graphics", create_graphics_tab() );
        tabs_panel.add( "Avionics", create_avionics_options_tab() );
        tabs_panel.add( "PFD", create_pfd_options_tab() );
        tabs_panel.add( "ND", create_nd_options_tab() );
        tabs_panel.add( "EGPWS", create_egpws_options_tab() );
        tabs_panel.add( "WX Radar", create_wxr_options_tab() );
        tabs_panel.add( "EICAS", create_eicas_options_tab() );
        tabs_panel.add( "MFD", create_mfd_options_tab() );
        tabs_panel.add( "CDU", create_cdu_options_tab() );

        return tabs_panel;

    }


	private JPanel create_system_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel system_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);
        cons.gridwidth = 1;

        int dialog_line = 0;

//        // Simulator communication
//        cons.gridx = 0;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.EAST;
//        system_panel.add(new JLabel("Simulator communication", JLabel.TRAILING), cons);
//        cons.gridx = 2;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.WEST;
//        this.simcom_combobox = new JComboBox();
//        this.simcom_combobox.addItem(XHSIPreferences.XHSI_PLUGIN);
//// this.simcom_combobox.addItem(XHSIPreferences.SCS);
//        this.simcom_combobox.addActionListener(this);
//        system_panel.add(this.simcom_combobox, cons);
//        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // AptNav Resources directory
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("X-Plane directory", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.aptnav_dir_textfield = new JTextField(40);
        system_panel.add(this.aptnav_dir_textfield, cons);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        JButton browse_button = new JButton("Browse");
        browse_button.setActionCommand("nav_browse");
        browse_button.addActionListener(this);
        system_panel.add(browse_button, cons);
        dialog_line++;
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(can be the local X-Plane directory, or shared over the network)", JLabel.TRAILING), cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // EGPWS Database directory
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("EGPWS database directory", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.egpws_db_dir_textfield = new JTextField(40);
        system_panel.add(this.egpws_db_dir_textfield, cons);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        JButton egpws_browse_button = new JButton("Browse");
        egpws_browse_button.setActionCommand("egpws_browse");
        egpws_browse_button.addActionListener(this);
        system_panel.add(egpws_browse_button, cons);
        dialog_line++;
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(Download GLOBE database from noaa.gov)", JLabel.TRAILING), cons);
        dialog_line++;
        
        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;
        
        // incoming UDP port
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Incoming UDP port (default 49020) (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.port_textfield = new JTextField(5);
        system_panel.add(this.port_textfield, cons);
        dialog_line++;

        // some info concerning Incoming UDP port
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(must match XHSI_plugin's Destination UDP port)", JLabel.TRAILING), cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // incoming UDP port
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Incoming Weather UDP port (default 48003) (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.weather_port_textfield = new JTextField(5);
        system_panel.add(this.weather_port_textfield, cons);
        dialog_line++;

        // some info concerning Incoming Weather UDP port
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(must match X-Plane control pad Destination UDP port)", JLabel.TRAILING), cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;
        
        // Multicast
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Enable multicast (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.multicast_checkbox = new JCheckBox("  (experimental)");
        system_panel.add(this.multicast_checkbox, cons);
        dialog_line++;
        
        // Multicast group
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Multicast group (default 239.255.0.120) (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.group_textfield = new JTextField(16);
        system_panel.add(this.group_textfield, cons);
        dialog_line++;

        // some info concerning Multicast group
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.gridwidth = 1;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(choose a multicast group address in the 239.xxx.yyy.zzz range)", JLabel.TRAILING), cons);
        dialog_line++;
                
        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Logging Level
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Logging Level", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.loglevel_combobox = new JComboBox<Level>();
        for ( int i=0; i!=loglevels.length; i++) this.loglevel_combobox.addItem( loglevels[i] );
        system_panel.add(this.loglevel_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // pilot/co-pilot/instructor
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Menu settings control the displays for ...", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.operator_combobox = new JComboBox<String>();
        this.operator_combobox.addItem("Pilot (standard X-Plane settings)");
        this.operator_combobox.addItem("Copilot (XHSI's extra settings)");
        this.operator_combobox.addItem("Instructor (independent settings)");
        this.operator_combobox.addActionListener(this);
        system_panel.add(this.operator_combobox, cons);
        dialog_line++;

        // Remote Shutdown
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Allow remote shutdown", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.allow_shutdown = new JCheckBox("");
        system_panel.add(this.allow_shutdown, cons);
        dialog_line++;
        
        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // A reminder
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        dialog_line++;

        return system_panel;

    }


    private JPanel create_windows_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel windows_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Start with "Keep window on top"
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Set \"Windows on top\" at startup", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.start_ontop_checkbox = new JCheckBox();
        windows_panel.add(this.start_ontop_checkbox, cons);
        dialog_line++;

        // Draw window frame
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Hide window title bar and frame", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.hide_window_frame_checkbox = new JCheckBox("  (requires a restart)");
        windows_panel.add(this.hide_window_frame_checkbox, cons);
        dialog_line++;

        // Lock the window positions and sizes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Lock instrument windows position and size", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_locked_checkbox = new JCheckBox("  (values below...)");
        this.panel_locked_checkbox.setActionCommand("locktoggle");
        this.panel_locked_checkbox.addActionListener(this);
        windows_panel.add(this.panel_locked_checkbox, cons);
        dialog_line++;

        // Get current window positions and sizes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Get current window positions and sizes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        get_button = new JButton("Get");
        get_button.setActionCommand("getwindow");
        get_button.addActionListener(this);
        windows_panel.add(get_button, cons);
        dialog_line++;


        JPanel sub_panel = new JPanel(new GridBagLayout());
        GridBagConstraints subcons = new GridBagConstraints();

        int col = 2;
        String[] colheader = { " Display ", " Left ", " Top ", " Width ", " Height ", " Border ", " Square ", " Orientation " };
        for (String head : colheader) {
                subcons.gridx = col;
                subcons.gridwidth = 1;
                subcons.gridy = 0;
                subcons.anchor = GridBagConstraints.CENTER;
                sub_panel.add(new JLabel(head, JLabel.TRAILING), subcons);
                col++;
        }

        for (XHSIInstrument.DU instrum : XHSIInstrument.DU.values() ) {

            int i = instrum.get_id();
            String descr = XHSIInstrument.DU.values()[i].get_name();
            int subdialog_column = 2;
            int subdialog_line = i + 1;

            // Instrument name
            subcons.gridx = 0;
            subcons.gridwidth = 1;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.EAST;
            sub_panel.add(new JLabel(descr, JLabel.TRAILING), subcons);

            // Activate
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_active_checkbox[i] = new JCheckBox();
            this.panel_active_checkbox[i].setActionCommand("drawtoggle");
            this.panel_active_checkbox[i].addActionListener(this);
            this.panel_active_checkbox[i].setToolTipText("Display the " + descr);
            sub_panel.add(this.panel_active_checkbox[i], subcons);
            subdialog_column++;

            // panel position x
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_pos_x_textfield[i] = new JTextField(4);
            this.panel_pos_x_textfield[i].setToolTipText("Horizontal window position for " + descr);
            sub_panel.add(this.panel_pos_x_textfield[i], subcons);
            subdialog_column++;

            // panel position y
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_pos_y_textfield[i] = new JTextField(4);
            this.panel_pos_y_textfield[i].setToolTipText("Vertical window position for " + descr);
            sub_panel.add(this.panel_pos_y_textfield[i], subcons);
            subdialog_column++;

            // panel width
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_width_textfield[i] = new JTextField(4);
            this.panel_width_textfield[i].setToolTipText("Window width for " + descr);
            sub_panel.add(this.panel_width_textfield[i], subcons);
            subdialog_column++;

            // panel height
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_height_textfield[i] = new JTextField(4);
            this.panel_height_textfield[i].setToolTipText("Window height for " + descr);
            sub_panel.add(this.panel_height_textfield[i], subcons);
            subdialog_column++;

            // panel border
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_border_textfield[i] = new JTextField(3);
            this.panel_border_textfield[i].setToolTipText("Border size for " + descr);
            if ( i > 0 ) sub_panel.add(this.panel_border_textfield[i], subcons);
            subdialog_column++;

            // Draw square window
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_square_checkbox[i] = new JCheckBox();
            this.panel_square_checkbox[i].setToolTipText("Keep the instrument display for " + descr + " square");
            if ( i > 0 ) sub_panel.add(this.panel_square_checkbox[i], subcons);
            subdialog_column++;

            // orientation
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_orientation_combobox[i] = new JComboBox<String>();
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.UP.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.LEFT.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.RIGHT.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.DOWN.get_rotation() );
            this.panel_orientation_combobox[i].addActionListener(this);
            this.panel_orientation_combobox[i].setToolTipText("Window orientation for " + descr);
            if ( i > 0 ) sub_panel.add(this.panel_orientation_combobox[i], subcons);
            subdialog_column++;

        }

        cons.gridx = 0;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(sub_panel, cons);
        dialog_line++;

        // Minimize control/command/status window at startup
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Minimize command window at startup", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.conwin_minimized_checkbox = new JCheckBox();
        windows_panel.add(this.conwin_minimized_checkbox, cons);
        dialog_line++;

        
        return windows_panel;

    }


    private JPanel create_graphics_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel graphics_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Border style
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Border style", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.border_style_combobox = new JComboBox<String>();
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_RELIEF);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_LIGHT);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_DARK);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_NONE);
        this.border_style_combobox.addActionListener(this);
        graphics_panel.add(this.border_style_combobox, cons);
        dialog_line++;

        // Border color
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Border color", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.border_color_combobox = new JComboBox<String>();
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_GRAY);
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_BROWN);
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_BLUE);
        this.border_color_combobox.addActionListener(this);
        graphics_panel.add(this.border_color_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        graphics_panel.add(new JLabel(" ", JLabel.TRAILING), cons);

        // Use more color variations
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Use more color nuances", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_more_color_checkbox = new JCheckBox();
        graphics_panel.add(this.use_more_color_checkbox, cons);
        dialog_line++;

        // Instruments fonts
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Instruments font", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.instruments_font_combobox = new JComboBox<String>();
        for (int i=0; i<instruments_fonts.length; i++) {
            this.instruments_font_combobox.addItem(this.instruments_fonts[i]);
        }
        this.instruments_font_combobox.addActionListener(this);
        graphics_panel.add(this.instruments_font_combobox, cons);
        dialog_line++;
        
        // CDU fonts
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("CDU font", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.cdu_font_combobox = new JComboBox<String>();
        for (int i=0; i<cdu_fonts.length; i++) {
            this.cdu_font_combobox.addItem(this.cdu_fonts[i]);
        }
        this.cdu_font_combobox.addActionListener(this);
        graphics_panel.add(this.cdu_font_combobox, cons);
        dialog_line++;
        
        // Bold fonts
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Bold fonts", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.bold_fonts_checkbox = new JCheckBox();
        graphics_panel.add(this.bold_fonts_checkbox, cons);
        dialog_line++;

        // Anti-alias
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Anti-aliasing", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.anti_alias_checkbox = new JCheckBox();
        graphics_panel.add(this.anti_alias_checkbox, cons);
        dialog_line++;

        // Bezier curves for the pavements
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Draw taxiways and aprons using bezier curves", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_bezier_pavements_checkbox = new JCheckBox();
        graphics_panel.add(this.draw_bezier_pavements_checkbox, cons);
        dialog_line++;

//        // A reminder
//        cons.gridx = 3;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        graphics_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return graphics_panel;

    }


    private JPanel create_avionics_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel avionics_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);
        cons.gridwidth = 1;

        int dialog_line = 0;

        // instrument style
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Instrument style", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.instrument_style_combobox = new JComboBox<String>();
        this.instrument_style_combobox.addItem("Switchable");
        this.instrument_style_combobox.addItem("Boeing");
        this.instrument_style_combobox.addItem("Airbus");
        this.instrument_style_combobox.addActionListener(this);
        avionics_options_panel.add(this.instrument_style_combobox, cons);
        dialog_line++;
        
        // Digital clock
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Digital Clock", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.digital_clock_checkbox = new JCheckBox("  (unchecked = analog clock)");
        avionics_options_panel.add(this.digital_clock_checkbox, cons);
        dialog_line++;
        
        // Empty line for spacing
        cons.gridx = 0;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Minimum runway length
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Airport minimum runway length", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.min_rwy_textfield = new JTextField(4);
        avionics_options_panel.add(this.min_rwy_textfield, cons);
        dialog_line++;
        
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Runway length units", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.rwy_units_combobox = new JComboBox<String>();
        this.rwy_units_combobox.addItem("meters");
        this.rwy_units_combobox.addItem("feet");
        this.rwy_units_combobox.addActionListener(this);
        avionics_options_panel.add(this.rwy_units_combobox, cons);
        dialog_line++;
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        avionics_options_panel.add(new JLabel("(override of minimum runway length and units possible with datarefs)", JLabel.TRAILING), cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Use avionics power
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Use battery and avionics power", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_power_checkbox = new JCheckBox("  (annunciators need battery power; displays need avionics power)");
        avionics_options_panel.add(this.use_power_checkbox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Auto-set CRS for LOC/ILS
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Auto-sync CRS for LOC/ILS", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.auto_frontcourse_checkbox = new JCheckBox("  (set the CRS (=OBS) automatically to the Localizer or ILS frontcourse)");
        avionics_options_panel.add(this.auto_frontcourse_checkbox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // HSI source
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("HSI source", JLabel.TRAILING), cons);
        
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.hsi_source_combobox = new JComboBox<String>();
        this.hsi_source_combobox.addItem("Switchable");
        this.hsi_source_combobox.addItem("Always NAV1");
        this.hsi_source_combobox.addItem("Always NAV2");
        this.hsi_source_combobox.addActionListener(this);
        avionics_options_panel.add(this.hsi_source_combobox, cons);
        dialog_line++;

        return avionics_options_panel;

    }


    private JPanel create_pfd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel pfd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Horizon style
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Horizon style (Boeing only)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.horizon_style_combobox = new JComboBox<String>();
        this.horizon_style_combobox.addItem("Square");
        this.horizon_style_combobox.addItem("Rounded square");
        this.horizon_style_combobox.addItem("Full width");
        this.horizon_style_combobox.addItem("Full screen");
//        this.horizon_style_combobox.addItem("Airbus");        
        this.horizon_style_combobox.addActionListener(this);
        pfd_options_panel.add(this.horizon_style_combobox, cons);
        dialog_line++;

        // Dial transparency
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Dial transparency %", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.dial_transparency_combobox = new JComboBox<String>();
        this.dial_transparency_combobox.addItem(transparencies[0]);
        this.dial_transparency_combobox.addItem(transparencies[1]);
        this.dial_transparency_combobox.addItem(transparencies[2]);
        this.dial_transparency_combobox.addItem(transparencies[3]);
        this.dial_transparency_combobox.addActionListener(this);
        pfd_options_panel.add(this.dial_transparency_combobox, cons);
        dialog_line++;

        // Draw an HSI below the AI
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw a full HSI below the attitude indicator", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.pfd_hsi_checkbox = new JCheckBox();
        pfd_options_panel.add(this.pfd_hsi_checkbox, cons);
        dialog_line++;

        // V-bar FD instead of crosshairs
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("V-bar FD instead of crosshairs", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_single_cue_fd_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_single_cue_fd_checkbox, cons);
        dialog_line++;

        // Draw AOA-indicator, pushing a (crippled) RA-indicator to the bottom
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw AOA-indicator", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_aoa_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_aoa_checkbox, cons);
        dialog_line++;

        // Draw Radios
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Display Radio frequencies", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_radios_checkbox = new JCheckBox("  (window aspect ratio must be 4/3 or wider)");
        pfd_options_panel.add(this.draw_radios_checkbox, cons);
        dialog_line++;

        // Center ADI horizontally
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Center ADI horizontally", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.adi_centered_checkbox = new JCheckBox("  (if window is wide enough) (disables DG/HSI)");
        pfd_options_panel.add(this.adi_centered_checkbox, cons);
        dialog_line++;

        // Draw Vmca (red) and Vyse (blue) lines
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw Vmca (red) and Vyse (blue) lines", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_twinspeeds_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_twinspeeds_checkbox, cons);
        dialog_line++;

        // Draw turn rate indicator
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw turn rate indicator", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_turnrate_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_turnrate_checkbox, cons);
        dialog_line++;

        // Draw G-meter
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw G-meter", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_gmeter_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_gmeter_checkbox, cons);
        dialog_line++;

        // Draw yoke and rudder input
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw yoke input", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_yoke_input_combobox = new JComboBox<String>();
        this.draw_yoke_input_combobox.addItem("None");
        this.draw_yoke_input_combobox.addItem("Auto - Yoke only");
        this.draw_yoke_input_combobox.addItem("Auto - Yoke, rudder, brakes");
        this.draw_yoke_input_combobox.addItem("Always - Yoke only");
        this.draw_yoke_input_combobox.addItem("Always - Yoke, rudder, brakes");
        this.draw_yoke_input_combobox.addActionListener(this);
        pfd_options_panel.add(this.draw_yoke_input_combobox, cons);
        dialog_line++;
        
        // Speed units
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("IAS Speed Unit", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.speed_unit_combobox = new JComboBox<String>();
        this.speed_unit_combobox.addItem("Kts");
        this.speed_unit_combobox.addItem("Km/h");
        this.speed_unit_combobox.addItem("Both");
        this.speed_unit_combobox.addActionListener(this);
        pfd_options_panel.add(this.speed_unit_combobox, cons);
        dialog_line++;
        
//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        efb_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return pfd_options_panel;

    }


    private JPanel create_nd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel nd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Pseudo Airbus display modes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Airbus display modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.airbus_modes_checkbox = new JCheckBox("  ( ROSE_ILS / ROSE_VOR / ROSE_NAV / ARC / PLAN )");
        nd_options_panel.add(this.airbus_modes_checkbox, cons);
        dialog_line++;

        // Symbols multiselection
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Symbols Multiselection", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.symbols_multiselection_checkbox = new JCheckBox("  (unchecked: Airbus radio buttons, checked Boeing push buttons)");
        nd_options_panel.add(this.symbols_multiselection_checkbox, cons);
        dialog_line++;

        // Display Centered APP and VOR as a classic HSI without moving map
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Display Centered APP and VOR as a classic-style HSI", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.classic_hsi_checkbox = new JCheckBox("  (as in a real B737-NG)");
        nd_options_panel.add(this.classic_hsi_checkbox, cons);
        dialog_line++;

        // Classic-style HSI course pointer follows nav-source color
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Classic-style HSI course pointer follows nav-source color", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.colored_hsi_course_checkbox = new JCheckBox();
        nd_options_panel.add(this.colored_hsi_course_checkbox, cons);
        dialog_line++;

        // Keep moving map in APP and VOR modes uncluttered
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Display only tuned navaids in APP and VOR modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.appvor_uncluttered_checkbox = new JCheckBox("  (to keep the moving map in APP and VOR modes uncluttered)");
        nd_options_panel.add(this.appvor_uncluttered_checkbox, cons);
        dialog_line++;

        // Display app/vor frequency mismatch caution message
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Warn for EFIS MODE/NAV FREQ mismatch", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.mode_mismatch_caution_checkbox = new JCheckBox();
        nd_options_panel.add(this.mode_mismatch_caution_checkbox, cons);
        dialog_line++;

        // Center PLAN mode on waypoint
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Center PLAN mode on aircraft", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.plan_aircraft_center_checkbox = new JCheckBox();
        nd_options_panel.add(this.plan_aircraft_center_checkbox, cons);
        dialog_line++;

        // Draw map only inside the compass rose
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw map only inside the compass rose", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_inside_rose_checkbox = new JCheckBox();
        nd_options_panel.add(this.draw_inside_rose_checkbox, cons);
        dialog_line++;

        // Draw range arcs
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw range arcs", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_range_arcs_checkbox = new JCheckBox();
        nd_options_panel.add(this.draw_range_arcs_checkbox, cons);
        dialog_line++;

        // Limit arcs in expanded mode to 60 degrees
        // This option is deprecated
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        // nd_options_panel.add(new JLabel("Limit arcs in expanded mode to 60\u00B0", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.limit_arcs_60deg_checkbox = new JCheckBox("  (applies only with option \"Draw map only inside the compass rose\" set)");
        // nd_options_panel.add(this.limit_arcs_60deg_checkbox, cons);
        // dialog_line++;

        // Limit arcs in expanded mode to selected degrees
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Limits arcs in expended mode to ", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.limit_arcs_deg_combobox = new JComboBox<String>();
        this.limit_arcs_deg_combobox.addItem("None");
        this.limit_arcs_deg_combobox.addItem("60\u00B0");
        this.limit_arcs_deg_combobox.addItem("70\u00B0");
        this.limit_arcs_deg_combobox.addItem("75\u00B0");
        this.limit_arcs_deg_combobox.addItem("80\u00B0");
        this.limit_arcs_deg_combobox.addItem("85\u00B0");
        this.limit_arcs_deg_combobox.addItem("90\u00B0");
        this.limit_arcs_deg_combobox.addActionListener(this);
        nd_options_panel.add(this.limit_arcs_deg_combobox, cons);
        dialog_line++;
        
        // Draw runways at lowest map ranges
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw runways at map range 10 and 20", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_rwy_checkbox = new JCheckBox("  (runways are always drawn at map ranges 6.4 and below)");
        nd_options_panel.add(this.draw_rwy_checkbox, cons);
        dialog_line++;

        // TCAS always ON
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("TCAS always ON", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.tcas_always_on_checkbox = new JCheckBox();
        nd_options_panel.add(this.tcas_always_on_checkbox, cons);
        dialog_line++;

        // Display navaid frequencies when DATA is on
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Display navaid frequencies and airport altitudes on the map", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_navaid_frequencies = new JCheckBox("  (when DATA is selected)");
        nd_options_panel.add(this.nd_navaid_frequencies, cons);
        dialog_line++;

        // Write AP heading at the top
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Write the AP heading at the top", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_write_ap_hdg = new JCheckBox();
        nd_options_panel.add(this.nd_write_ap_hdg, cons);
        dialog_line++;

        // Show the Clock/Chronograph at the bottom
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Show the Clock/Chronograph at the bottom", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_show_clock = new JCheckBox();
        nd_options_panel.add(this.nd_show_clock, cons);
        dialog_line++;

        // Load and display helipads
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Load and display helipads", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_show_helipads = new JCheckBox("  (requires a restart)");
        nd_options_panel.add(this.nd_show_helipads, cons);
        dialog_line++;
        
        
//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.EAST;
//        nd_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return nd_options_panel;

    }


    private JPanel create_egpws_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel egpws_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;
        
        // Terrain sweep
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        egpws_options_panel.add(new JLabel("Activate sweep", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_terrain_sweep = new JCheckBox();
        egpws_options_panel.add(this.nd_terrain_sweep, cons);
        dialog_line++;
        
        // Terrain sweep bar
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        egpws_options_panel.add(new JLabel("Display sweep bar", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_terrain_sweep_bar = new JCheckBox();
        egpws_options_panel.add(this.nd_terrain_sweep_bar, cons);
        dialog_line++;
        
        // Terrain sweep rate
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        egpws_options_panel.add(new JLabel("Sweep duration (seconds)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_terrain_sweep_time = new JTextField(2);
        egpws_options_panel.add(this.nd_terrain_sweep_time, cons);
        dialog_line++;
        
        // Terrain resolution
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        egpws_options_panel.add(new JLabel("Terrain resolution", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;      
        terrain_resolution_combobox = new JComboBox<String>();
        this.terrain_resolution_combobox.addItem("Fine");
        this.terrain_resolution_combobox.addItem("Medium");
        this.terrain_resolution_combobox.addItem("Coarse");
        egpws_options_panel.add(this.terrain_resolution_combobox, cons);
        dialog_line++;
        
        // Terrain auto-display
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        // egpws_options_panel.add(new JLabel("EGPWS caution displays terrain", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_terrain_auto_display = new JCheckBox();
        // TODO: feature to be implemented in the next version
        // egpws_options_panel.add(this.nd_terrain_auto_display, cons);
        dialog_line++;

        // Terrain peak mode
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        // egpws_options_panel.add(new JLabel("Terrain peaks mode", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_terrain_peaks_mode = new JCheckBox();
        // TODO: feature to be implemented in the next version
        // egpws_options_panel.add(this.nd_terrain_peaks_mode, cons);
        dialog_line++;
        
        // Vertical path
        
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        // egpws_options_panel.add(new JLabel("Display vertical path bellow ND", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_show_vertical_path = new JCheckBox();
        // egpws_options_panel.add(this.nd_show_vertical_path, cons);
        dialog_line++;
        
        //      // A reminder
        //      cons.gridx = 2;
        //      cons.gridwidth = 1;
        //      cons.gridy = dialog_line;
        //      cons.anchor = GridBagConstraints.EAST;
        //      nd_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        //      dialog_line++;

      return egpws_options_panel;

  }
   

    private JPanel create_wxr_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel wxr_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;
        
        // Weather radar resolution
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        wxr_options_panel.add(new JLabel("Radar resolution", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;      
        wxr_resolution_combobox = new JComboBox<String>();
        this.wxr_resolution_combobox.addItem("Fine");
        this.wxr_resolution_combobox.addItem("Medium");
        this.wxr_resolution_combobox.addItem("Coarse");
        wxr_options_panel.add(this.wxr_resolution_combobox, cons);
        dialog_line++;

        // Weather radar sweep
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        wxr_options_panel.add(new JLabel("Activate sweep", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_wxr_sweep = new JCheckBox();
        wxr_options_panel.add(this.nd_wxr_sweep, cons);
        dialog_line++;
        
        // Weather radar sweep bar
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        wxr_options_panel.add(new JLabel("Display sweep bar", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_wxr_sweep_bar = new JCheckBox();
        wxr_options_panel.add(this.nd_wxr_sweep_bar, cons);
        dialog_line++;
        
        // Weather radar sweep duration
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        wxr_options_panel.add(new JLabel("Sweep duration (seconds)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_wxr_sweep_time = new JTextField(2);
        wxr_options_panel.add(this.nd_wxr_sweep_time, cons);
        dialog_line++;

        // Weather radar dual settings
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        // wxr_options_panel.add(new JLabel("Activate dual pilot and copilot settings", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_wxr_dual_settings = new JCheckBox();
        // TODO: feature to implement in the next version
        // wxr_options_panel.add(this.nd_wxr_dual_settings, cons);
        dialog_line++;
        
        // Weather radar color gradiant
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        wxr_options_panel.add(new JLabel("More than 4 colors gradiant", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.nd_wxr_color_gradient = new JCheckBox();
        wxr_options_panel.add(this.nd_wxr_color_gradient, cons);
        dialog_line++;
        
        //      // A reminder
        //      cons.gridx = 2;
        //      cons.gridwidth = 1;
        //      cons.gridy = dialog_line;
        //      cons.anchor = GridBagConstraints.EAST;
        //      nd_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        //      dialog_line++;

      return wxr_options_panel;

  }    
    

    private JPanel create_eicas_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel eicas_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // EICAS layout
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Layout", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.eicas_layout_combobox = new JComboBox<String>();
        this.eicas_layout_combobox.addItem("Primary engine instruments only");
        this.eicas_layout_combobox.addItem("Primary engine instruments and gear, flaps, etc...");
        this.eicas_layout_combobox.addItem("Primary and secondary engine instruments");
        eicas_options_panel.add(this.eicas_layout_combobox, cons);
        dialog_line++;

        // Number of engines
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Number of engines", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.engine_count_combobox = new JComboBox<String>();
        this.engine_count_combobox.addItem("Auto");
        this.engine_count_combobox.addItem("1");
        this.engine_count_combobox.addItem("2");
        this.engine_count_combobox.addItem("3");
        this.engine_count_combobox.addItem("4");
        this.engine_count_combobox.addItem("5");
        this.engine_count_combobox.addItem("6");
        this.engine_count_combobox.addItem("7");
        this.engine_count_combobox.addItem("8");
        eicas_options_panel.add(this.engine_count_combobox, cons);
        dialog_line++;

        // Engines type
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Engine type", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.engine_type_combobox = new JComboBox<String>();
        this.engine_type_combobox.addItem("Switchable");
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_N1);
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_EPR);
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_TRQ);
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_MAP);
        this.engine_type_combobox.addActionListener(this);
        eicas_options_panel.add(this.engine_type_combobox, cons);
        dialog_line++;

        // TRQ scale
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("TRQ scale", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.trq_scale_combobox = new JComboBox<String>();
        this.trq_scale_combobox.addItem("Switchable");
        this.trq_scale_combobox.addItem(XHSIPreferences.TRQ_SCALE_LBFT);
        this.trq_scale_combobox.addItem(XHSIPreferences.TRQ_SCALE_NM);
        this.trq_scale_combobox.addItem(XHSIPreferences.TRQ_SCALE_PERCENT);
        this.trq_scale_combobox.addActionListener(this);
        eicas_options_panel.add(this.trq_scale_combobox, cons);
        dialog_line++;

        // Fuel units
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Fuel units", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.fuel_unit_combobox = new JComboBox<String>();
        this.fuel_unit_combobox.addItem("Switchable");
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_UNITS_KG);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_UNITS_LBS);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_UNITS_USG);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_UNITS_LTR);
        this.fuel_unit_combobox.addActionListener(this);
        eicas_options_panel.add(this.fuel_unit_combobox, cons);
        dialog_line++;

        // Temp units
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Temperature units", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.temp_unit_combobox = new JComboBox<String>();
        this.temp_unit_combobox.addItem("Switchable");
        this.temp_unit_combobox.addItem(XHSIPreferences.TEMP_UNITS_CELCIUS);
        this.temp_unit_combobox.addItem(XHSIPreferences.TEMP_UNITS_FAHRENHEIT);
        // this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_UNITS_KELVIN);
        this.temp_unit_combobox.addActionListener(this);
        eicas_options_panel.add(this.temp_unit_combobox, cons);
        dialog_line++;
        
//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        efb_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return eicas_options_panel;

    }


    private JPanel create_mfd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel mfd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // MFD display mode
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        mfd_options_panel.add(new JLabel("MFD display mode", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.mfd_mode_combobox = new JComboBox<String>();
        this.mfd_mode_combobox.addItem("Switchable");
        this.mfd_mode_combobox.addItem("Linked (QPAC)");
        this.mfd_mode_combobox.addItem("Airport Chart");
        this.mfd_mode_combobox.addItem("Flight Plan");
        this.mfd_mode_combobox.addItem("RTU Display");
        this.mfd_mode_combobox.addItem("Lower EICAS");
        this.mfd_mode_combobox.addItem("Bleed air");
        this.mfd_mode_combobox.addItem("Pressurisation");
        this.mfd_mode_combobox.addItem("Electrics");
        this.mfd_mode_combobox.addItem("Hydraulics");
        this.mfd_mode_combobox.addItem("Fuel");
        this.mfd_mode_combobox.addItem("APU");
        this.mfd_mode_combobox.addItem("Air Conditionning");
        this.mfd_mode_combobox.addItem("Doors / Oxygen");
        this.mfd_mode_combobox.addItem("Wheels");
        this.mfd_mode_combobox.addItem("Flight Controls");
        this.mfd_mode_combobox.addItem("Cruise");
        this.mfd_mode_combobox.addItem("Status");
       
        this.mfd_mode_combobox.addActionListener(this);
        mfd_options_panel.add(this.mfd_mode_combobox, cons);
        dialog_line++;

        // Airport Diagram colors
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        mfd_options_panel.add(new JLabel("Airport Diagram day/night colors", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.arpt_chart_color_combobox = new JComboBox<String>();
        this.arpt_chart_color_combobox.addItem("Auto");
        this.arpt_chart_color_combobox.addItem("Day");
        this.arpt_chart_color_combobox.addItem("Night");
        this.arpt_chart_color_combobox.addActionListener(this);
        mfd_options_panel.add(this.arpt_chart_color_combobox, cons);
        dialog_line++;

        // Display airport that is set as NAV destination
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        mfd_options_panel.add(new JLabel("Display airport that is set as NAV destination", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.arpt_chart_nav_dest = new JCheckBox("  (otherwise the nearest airport)");
        mfd_options_panel.add(this.arpt_chart_nav_dest, cons);
        dialog_line++;

        return mfd_options_panel;

    }


    private JPanel create_cdu_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel cdu_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // CDU layout
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        cdu_options_panel.add(new JLabel("CDU display only, without keyboard", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.cdu_display_only = new JCheckBox();
        cdu_options_panel.add(this.cdu_display_only, cons);
        dialog_line++;

        // CDU source
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        cdu_options_panel.add(new JLabel("CDU source", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.cdu_source_combobox = new JComboBox<String>();
        this.cdu_source_combobox.addItem("Switchable");
        this.cdu_source_combobox.addItem("Aircraft's custom FMC (or a dummy)");
        this.cdu_source_combobox.addItem("X-FMC");        
        // TODO : for beta 9
        // this.cdu_source_combobox.addItem("UFMC or X737FMC");
        this.cdu_source_combobox.addActionListener(this);
        cdu_options_panel.add(this.cdu_source_combobox, cons);
        dialog_line++;

        // CDU side
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        cdu_options_panel.add(new JLabel("CDU side", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.cdu_side_combobox = new JComboBox<String>();
        this.cdu_side_combobox.addItem("Switchable");
        this.cdu_side_combobox.addItem("Left");
        this.cdu_side_combobox.addItem("Right");        
        this.cdu_side_combobox.addActionListener(this);
        cdu_options_panel.add(this.cdu_side_combobox, cons);
        dialog_line++;
        
        return cdu_options_panel;

    }


    private JPanel create_dialog_buttons_panel() {

        FlowLayout layout = new FlowLayout();
        JPanel preferences_panel = new JPanel(layout);

        JButton cancel_button = new JButton("Cancel");
        cancel_button.setActionCommand("cancel");
        cancel_button.addActionListener(this);

        JButton apply_button = new JButton("Apply");
        apply_button.setActionCommand("apply");
        apply_button.addActionListener(this);

        JButton ok_button = new JButton("OK");
        ok_button.setActionCommand("ok");
        ok_button.addActionListener(this);

        preferences_panel.add(cancel_button);
        preferences_panel.add(apply_button);
        preferences_panel.add(ok_button);

        return preferences_panel;

    }


    public void actionPerformed(ActionEvent event) {

        if ( event.getActionCommand().equals("nav_browse") ) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                this.aptnav_dir_textfield.setText(file.getAbsolutePath());
            }
        } else if ( event.getActionCommand().equals("egpws_browse") ) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                this.egpws_db_dir_textfield.setText(file.getAbsolutePath());
            }    
        } else if ( event.getActionCommand().equals("drawtoggle")) {
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("locktoggle")) {
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("getwindow")) {
            for (XHSIInstrument du : this.flightdeck) {
                int i = du.get_index();
                this.panel_pos_x_textfield[i].setText( "" + du.frame.getX() );
                this.panel_pos_y_textfield[i].setText( "" + du.frame.getY() );
                this.panel_width_textfield[i].setText( "" + du.frame.getWidth() );
                this.panel_height_textfield[i].setText( "" + du.frame.getHeight() );
            }
        } else if ( event.getActionCommand().equals("cancel") ) {
            this.setVisible(false);
            init_preferences();
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("apply") ) {
            if ( set_preferences() ) {
                resize_frames();
            }
        } else if ( event.getActionCommand().equals("ok") ) {
            if ( set_preferences() ) {
                this.setVisible(false);
                resize_frames();
            }
        }

    }


    private void enable_lock_fields() {
        for (int i=0; i<MAX_WINS; i++) {
            boolean active = this.panel_active_checkbox[i].isSelected();
            boolean lock = this.panel_locked_checkbox.isSelected();
            //this.get_button.setEnabled( this.panel_locked_checkbox.isSelected() );
            this.panel_pos_x_textfield[i].setEnabled( active & lock );
            this.panel_pos_y_textfield[i].setEnabled( active & lock );
            this.panel_width_textfield[i].setEnabled( active & lock );
            this.panel_height_textfield[i].setEnabled( active & lock );
            this.panel_border_textfield[i].setEnabled( active & lock );
            this.panel_square_checkbox[i].setEnabled( active );
            this.panel_orientation_combobox[i].setEnabled( active );
        }
    }


    private void resize_frames() {

        if ( this.panel_locked_checkbox.isSelected() ) {
            for (XHSIInstrument du : this.flightdeck) {
                int i = du.get_index();
                du.frame.setBounds(this.du_pos_x[i], this.du_pos_y[i], this.du_width[i], this.du_height[i]);
                boolean active = this.panel_active_checkbox[i].isSelected();
                if ( du.frame.isVisible() != active ) {
                    du.frame.setVisible( active );
                }
                switch ( du.get_index() ) {
                    case XHSIInstrument.EMPTY_ID :
                        ((EmptyComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.PFD_ID :
                        ((PFDComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.ND_ID :
                        ((NDComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.EICAS_ID :
                        ((EICASComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.MFD_ID :
                        ((MFDComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.ANNUN_ID :
                        ((AnnunComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.CLOCK_ID :
                        ((ClockComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.CDU_ID :
                        ((CDUComponent)du.components).forceReconfig();
                        break;
                }
            }
        }

    }


    private boolean set_preferences() {

        boolean valid = fields_valid();
        if ( ! valid ) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid Preferences",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            // SYSTEM

            int loglevel_index = this.loglevel_combobox.getSelectedIndex();
            Level loglevel = this.loglevels[loglevel_index];
            logger.setLevel(loglevel);
            this.preferences.set_preference(XHSIPreferences.PREF_LOGLEVEL, loglevel.toString());

//            if ( ! simcoms[this.simcom_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_SIMCOM)) )
//                this.preferences.set_preference(XHSIPreferences.PREF_SIMCOM, borderstyles[this.simcom_combobox.getSelectedIndex()]);

            if ( this.aptnav_dir_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_APTNAV_DIR, this.aptnav_dir_textfield.getText());
            
            if ( this.egpws_db_dir_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_EGPWS_DB_DIR)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_EGPWS_DB_DIR, this.egpws_db_dir_textfield.getText());
            
            if ( this.port_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_PORT)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_PORT, this.port_textfield.getText());

            if ( this.weather_port_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_WEATHER_PORT)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_WEATHER_PORT, this.weather_port_textfield.getText());

            if ( this.group_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_GROUP)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_GROUP, this.group_textfield.getText());

            if ( this.multicast_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_MULTICAST).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_MULTICAST, this.multicast_checkbox.isSelected()?"true":"false");
            
            if ( ! operators[this.operator_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION)) )
                this.preferences.set_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION, operators[this.operator_combobox.getSelectedIndex()]);
 
            if ( this.allow_shutdown.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ALLOW_SHUTDOWN).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ALLOW_SHUTDOWN, this.allow_shutdown.isSelected()?"true":"false");
 

            // GRAPHICS


            if ( this.use_more_color_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_MORE_COLOR, this.use_more_color_checkbox.isSelected()?"true":"false");

            if ( this.bold_fonts_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_BOLD_FONTS, this.bold_fonts_checkbox.isSelected()?"true":"false");

            if ( this.anti_alias_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ANTI_ALIAS, this.anti_alias_checkbox.isSelected()?"true":"false");

            if ( ! borderstyles[this.border_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_BORDER_STYLE, borderstyles[this.border_style_combobox.getSelectedIndex()]);

            if ( ! bordercolors[this.border_color_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_BORDER_COLOR)) )
                this.preferences.set_preference(XHSIPreferences.PREF_BORDER_COLOR, bordercolors[this.border_color_combobox.getSelectedIndex()]);

            if ( ! instruments_fonts[this.instruments_font_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_INSTRUMENTS_FONT)) )
                this.preferences.set_preference(XHSIPreferences.PREF_INSTRUMENTS_FONT, instruments_fonts[this.instruments_font_combobox.getSelectedIndex()]);

            if ( ! cdu_fonts[this.cdu_font_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_CDU_FONT)) )
                this.preferences.set_preference(XHSIPreferences.PREF_CDU_FONT, cdu_fonts[this.cdu_font_combobox.getSelectedIndex()]);

            if ( this.draw_bezier_pavements_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS, this.draw_bezier_pavements_checkbox.isSelected()?"true":"false");


            // WINDOWS

            if ( this.start_ontop_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_START_ONTOP, this.start_ontop_checkbox.isSelected()?"true":"false");

            if ( this.hide_window_frame_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES, this.hide_window_frame_checkbox.isSelected()?"true":"false");

            if ( this.panel_locked_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PANELS_LOCKED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PANELS_LOCKED , this.panel_locked_checkbox.isSelected()?"true":"false");

            for (int i=0; i<MAX_WINS; i++) {
                if ( this.panel_active_checkbox[i].isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ACTIVE).equals("true") )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ACTIVE, this.panel_active_checkbox[i].isSelected()?"true":"false" );
                if ( ! this.panel_pos_x_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_X) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_X , this.panel_pos_x_textfield[i].getText() );
                if ( ! this.panel_pos_y_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_Y) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_Y , this.panel_pos_y_textfield[i].getText() );
                if ( ! this.panel_width_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_WIDTH) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_WIDTH , this.panel_width_textfield[i].getText() );
                if ( ! this.panel_height_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_HEIGHT) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_HEIGHT , this.panel_height_textfield[i].getText() );
                if ( ! this.panel_border_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_BORDER) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_BORDER , this.panel_border_textfield[i].getText() );
                if ( this.panel_square_checkbox[i].isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_SQUARE).equals("true") )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_SQUARE, this.panel_square_checkbox[i].isSelected()?"true":"false" );
                if ( ! orientations[this.panel_orientation_combobox[i].getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ORIENTATION)) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ORIENTATION, orientations[this.panel_orientation_combobox[i].getSelectedIndex()] );
            }

            if ( this.conwin_minimized_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CONWIN_MINIMIZED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CONWIN_MINIMIZED , this.conwin_minimized_checkbox.isSelected()?"true":"false");


            // Avionics options
            if ( ! instrument_styles[this.instrument_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE, instrument_styles[this.instrument_style_combobox.getSelectedIndex()]);

            if ( this.digital_clock_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CLOCK_DISPLAY_DIGITAL).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CLOCK_DISPLAY_DIGITAL, this.digital_clock_checkbox.isSelected()?"true":"false");

            if ( this.use_power_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_POWER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_POWER, this.use_power_checkbox.isSelected()?"true":"false");

            if ( this.auto_frontcourse_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE, this.auto_frontcourse_checkbox.isSelected()?"true":"false");

            if ( this.hsi_source_combobox.getSelectedIndex() != this.preferences.get_hsi_source() )
                this.preferences.set_preference(XHSIPreferences.PREF_HSI_SOURCE, hsi_sources[this.hsi_source_combobox.getSelectedIndex()]);


            // ND options

            if ( this.min_rwy_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_MIN_RWY_LEN, this.min_rwy_textfield.getText());

            if ( ! units[this.rwy_units_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_RWY_LEN_UNITS, units[this.rwy_units_combobox.getSelectedIndex()]);

            if ( this.draw_rwy_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RUNWAYS, this.draw_rwy_checkbox.isSelected()?"true":"false");

            if ( this.airbus_modes_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_AIRBUS_MODES, this.airbus_modes_checkbox.isSelected()?"true":"false");
            
            if ( this.symbols_multiselection_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_SYMBOLS_MULTISELECTION).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_SYMBOLS_MULTISELECTION, this.symbols_multiselection_checkbox.isSelected()?"true":"false");

            if ( this.draw_range_arcs_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS, this.draw_range_arcs_checkbox.isSelected()?"true":"false");

            if ( this.limit_arcs_60deg_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_LIMIT_ARCS_60DEG).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_LIMIT_ARCS_60DEG, this.limit_arcs_60deg_checkbox.isSelected()?"true":"false");

            // TODO : Type mismatch !!!
            if ( this.limit_arcs_deg_combobox.getSelectedIndex() != this.preferences.get_limit_arcs_deg() )
                this.preferences.set_preference(XHSIPreferences.PREF_LIMIT_ARCS_DEG, arc_limits[this.limit_arcs_deg_combobox.getSelectedIndex()]);

            
            if ( this.mode_mismatch_caution_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION, this.mode_mismatch_caution_checkbox.isSelected()?"true":"false");

            if ( this.tcas_always_on_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON, this.tcas_always_on_checkbox.isSelected()?"true":"false");

            if ( this.classic_hsi_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CLASSIC_HSI, this.classic_hsi_checkbox.isSelected()?"true":"false");

            if ( this.appvor_uncluttered_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER, this.appvor_uncluttered_checkbox.isSelected()?"true":"false");

            if ( this.plan_aircraft_center_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER, this.plan_aircraft_center_checkbox.isSelected()?"true":"false");

            if ( this.draw_inside_rose_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE, this.draw_inside_rose_checkbox.isSelected()?"true":"false");

            if ( this.colored_hsi_course_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE, this.colored_hsi_course_checkbox.isSelected()?"true":"false");

            if ( this.nd_navaid_frequencies.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ND_NAVAID_FREQ).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_NAVAID_FREQ, this.nd_navaid_frequencies.isSelected()?"true":"false");

            if ( this.nd_write_ap_hdg.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ND_WRITE_AP_HDG).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_WRITE_AP_HDG, this.nd_write_ap_hdg.isSelected()?"true":"false");

            if ( this.nd_show_clock.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_CLOCK).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_SHOW_CLOCK, this.nd_show_clock.isSelected()?"true":"false");

            if ( this.nd_show_helipads.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_HELIPADS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_SHOW_HELIPADS, this.nd_show_helipads.isSelected()?"true":"false");

            // TODO: EGPWS options [ TERRAIN ]
            if ( this.nd_terrain_sweep.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_SWEEP, this.nd_terrain_sweep.isSelected()?"true":"false");
            if ( this.nd_terrain_sweep_bar.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_BAR).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_BAR, this.nd_terrain_sweep_bar.isSelected()?"true":"false");
            // TODO: sweep duration
            if ( ! this.nd_terrain_sweep_time.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_DURATION)) )
            	this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_SWEEP_DURATION, nd_terrain_sweep_time.getText() );
            if ( this.terrain_resolution_combobox.getSelectedIndex() != this.preferences.get_terrain_resolution() )
                this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_RESOLUTION, terrain_resolutions[this.terrain_resolution_combobox.getSelectedIndex()]);
            if ( this.nd_show_vertical_path.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ND_SHOW_VERTICAL_PATH).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_SHOW_VERTICAL_PATH, this.nd_show_vertical_path.isSelected()?"true":"false");
            if ( this.nd_terrain_auto_display.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TERRAIN_AUTO_DISPLAY).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_AUTO_DISPLAY, this.nd_terrain_auto_display.isSelected()?"true":"false");
            if ( this.nd_terrain_peaks_mode.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TERRAIN_PEAKS_MODE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TERRAIN_PEAKS_MODE, this.nd_terrain_peaks_mode.isSelected()?"true":"false");

            // TODO: Weather Radar options
            if ( this.wxr_resolution_combobox.getSelectedIndex() != this.preferences.get_nd_wxr_resolution() )
                this.preferences.set_preference(XHSIPreferences.PREF_WXR_RESOLUTION, wxr_resolutions[this.wxr_resolution_combobox.getSelectedIndex()]);
            if ( this.nd_wxr_sweep.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_WXR_SWEEP, this.nd_wxr_sweep.isSelected()?"true":"false");
            if ( this.nd_wxr_sweep_bar.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP_BAR).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_WXR_SWEEP_BAR, this.nd_wxr_sweep_bar.isSelected()?"true":"false");
            if ( ! this.nd_wxr_sweep_time.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_WXR_SWEEP_DURATION)) )
            	this.preferences.set_preference(XHSIPreferences.PREF_WXR_SWEEP_DURATION, nd_wxr_sweep_time.getText() );
            if ( this.nd_wxr_dual_settings.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_WXR_DUAL_SETTINGS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_WXR_DUAL_SETTINGS, this.nd_wxr_dual_settings.isSelected()?"true":"false");
            if ( this.nd_wxr_color_gradient.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_WXR_COLOR_GRADIENT).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_WXR_COLOR_GRADIENT, this.nd_wxr_color_gradient.isSelected()?"true":"false");

            
            // PFD options

            if ( ! horizons[this.horizon_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_HORIZON_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_HORIZON_STYLE, horizons[this.horizon_style_combobox.getSelectedIndex()]);

            if ( ! transparencies[this.dial_transparency_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY)) )
                this.preferences.set_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY, transparencies[this.dial_transparency_combobox.getSelectedIndex()]);

            if ( this.draw_single_cue_fd_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_SINGLE_CUE_FD).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_SINGLE_CUE_FD, this.draw_single_cue_fd_checkbox.isSelected()?"true":"false");

            if ( this.draw_aoa_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_AOA).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_AOA, this.draw_aoa_checkbox.isSelected()?"true":"false");

            if ( this.pfd_hsi_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_HSI).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_HSI, this.pfd_hsi_checkbox.isSelected()?"true":"false");

            if ( this.draw_radios_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS, this.draw_radios_checkbox.isSelected()?"true":"false");

            if ( this.adi_centered_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_ADI_CENTERED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_ADI_CENTERED, this.adi_centered_checkbox.isSelected()?"true":"false");

            if ( this.draw_twinspeeds_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_TWINSPEEDS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_TWINSPEEDS, this.draw_twinspeeds_checkbox.isSelected()?"true":"false");

            if ( this.draw_turnrate_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_TURNRATE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_TURNRATE, this.draw_turnrate_checkbox.isSelected()?"true":"false");

            if ( this.draw_gmeter_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_GMETER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_GMETER, this.draw_gmeter_checkbox.isSelected()?"true":"false");

            if ( ! draw_yoke_input[this.draw_yoke_input_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_YOKE_INPUT)) )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_YOKE_INPUT, draw_yoke_input[this.draw_yoke_input_combobox.getSelectedIndex()]);

            if ( ! speed_unit_input[this.speed_unit_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_PFD_SPEED_UNIT)) )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_SPEED_UNIT, speed_unit_input[this.speed_unit_combobox.getSelectedIndex()]);

           
            // EICAS options

            if ( ! eicas_layouts[this.eicas_layout_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_EICAS_LAYOUT)) )
                this.preferences.set_preference(XHSIPreferences.PREF_EICAS_LAYOUT, eicas_layouts[this.eicas_layout_combobox.getSelectedIndex()]);

            if ( this.engine_count_combobox.getSelectedIndex() != this.preferences.get_override_engine_count() )
                this.preferences.set_preference(XHSIPreferences.PREF_OVERRIDE_ENGINE_COUNT, "" + this.engine_count_combobox.getSelectedIndex());

            if ( ! engine_types[this.engine_type_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_ENGINE_TYPE, engine_types[this.engine_type_combobox.getSelectedIndex()]);

            if ( ! trq_scales[this.trq_scale_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_TRQ_SCALE, trq_scales[this.trq_scale_combobox.getSelectedIndex()]);

            if ( ! fuel_units[this.fuel_unit_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_FUEL_UNITS, fuel_units[this.fuel_unit_combobox.getSelectedIndex()]);

            if ( ! temp_units[this.temp_unit_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_TEMP_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_TEMP_UNITS, temp_units[this.temp_unit_combobox.getSelectedIndex()]);


            // MFD options

            if ( ! mfd_modes[this.mfd_mode_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_MFD_MODE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_MFD_MODE, mfd_modes[this.mfd_mode_combobox.getSelectedIndex()]);

            if ( ! arpt_chart_colors[this.arpt_chart_color_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR)) )
                this.preferences.set_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR, arpt_chart_colors[this.arpt_chart_color_combobox.getSelectedIndex()]);

            if ( this.arpt_chart_nav_dest.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_NAV_DEST).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ARPT_CHART_NAV_DEST, this.arpt_chart_nav_dest.isSelected()?"true":"false");

            
            // CDU options

            if ( this.cdu_display_only.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CDU_DISPLAY_ONLY).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CDU_DISPLAY_ONLY, this.cdu_display_only.isSelected()?"true":"false");

            if ( ! cdu_sources[this.cdu_source_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_CDU_SOURCE, cdu_sources[this.cdu_source_combobox.getSelectedIndex()]);

            if ( ! cdu_sides[this.cdu_side_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_CDU_SIDE, cdu_sides[this.cdu_side_combobox.getSelectedIndex()]);

        }

        return valid;

    }


    private boolean fields_valid() {

        this.field_validation_errors = new String();

        // Incoming port
        int port;
        try {
            port = Integer.parseInt(this.port_textfield.getText());
            if ((port < 1024) || (port > 65535)) {
                field_validation_errors += "Port out of range (1024-65535)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Port contains non-numeric characters!\n";
        }

        // Incoming weather port
        try {
            port = Integer.parseInt(this.weather_port_textfield.getText());
            if ((port < 1024) || (port > 65535)) {
                field_validation_errors += "Port out of range (1024-65535)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Port contains non-numeric characters!\n";
        }

        
        // minimum runway length
        int min_rwy;
        try {
            min_rwy = Integer.parseInt(this.min_rwy_textfield.getText());
            if ((min_rwy < 0) || (min_rwy > 9999)) {
                field_validation_errors += "Minimum Runway Length out of range (0-9999)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Minimum Runway Length contains non-numeric characters!\n";
        }

        // sweep rate (EGPWS and Weather Radar)
        int rate;
        try {
        	rate = Integer.parseInt(this.nd_terrain_sweep_time.getText());
            if ((rate < 1) || (rate > 30)) {
                field_validation_errors += "Sweep rate out of range [1-30]!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Sweep rate contains non-numeric characters!\n";
        }
        try {
        	rate = Integer.parseInt(this.nd_wxr_sweep_time.getText());
            if ((rate < 1) || (rate > 30)) {
                field_validation_errors += "Sweep rate out of range [1-30]!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Sweep rate contains non-numeric characters!\n";
        }
        
        for (int i=0; i<MAX_WINS; i++) {

            // Window horizontal position
            try {
                this.du_pos_x[i] = Integer.parseInt(this.panel_pos_x_textfield[i].getText());
                if ((this.du_pos_x[i] < -9999) || (this.du_pos_x[i] > 9999)) {
                    field_validation_errors += "Window horizontal position out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
            }
            // Window vertical position
            try {
                this.du_pos_y[i] = Integer.parseInt(this.panel_pos_y_textfield[i].getText());
                if ((this.du_pos_y[i] < -9999) || (this.du_pos_y[i] > 9999)) {
                    field_validation_errors += "Window horizontal position out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
            }
            // Window width
            try {
                this.du_width[i] = Integer.parseInt(this.panel_width_textfield[i].getText());
                if ((this.du_width[i] < this.flightdeck.get(i).du.get_min_width()) || (this.du_width[i] > 3999)) {
                    field_validation_errors += "Window width out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window width contains non-numeric characters!\n";
            }
            // Window height
            try {
                this.du_height[i] = Integer.parseInt(this.panel_height_textfield[i].getText());
                if ((this.du_height[i] < this.flightdeck.get(i).du.get_min_height()) || (this.du_height[i] > 1999)) {
                    field_validation_errors += "Window height out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window height contains non-numeric characters!\n";
            }
            // Border width
            int border;
            try {
                border = Integer.parseInt(this.panel_border_textfield[i].getText());
                if ( (border < 0) || (border > 399) || ((this.du_width[i] - 2*border) < this.flightdeck.get(i).du.get_min_width() - 20) || ((this.du_height[i] - 2*border) < this.flightdeck.get(i).du.get_min_height() - 20) ) {
                    field_validation_errors += "Instrument border out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window height contains non-numeric characters!\n";
            }

        }

        if (field_validation_errors.equals("") == false) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }

    }


}
