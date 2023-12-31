/**
* Avionics.java
* 
* Model for an aircraft's avionics systems
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2019  Nicolas Carel
* Copyright (C) 2018  Patrick Burkart (pburkartpublic@gmail.com) (Technische Hochschule Ingolstadt)
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
package net.sourceforge.xhsi.model;

import net.sourceforge.xhsi.XHSIInstrument.DU;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;


public interface Avionics {
	
	public enum InstrumentSide { PILOT, COPILOT, INSTRUCTOR };

    public static final int STYLE_BOEING = 0;
    public static final int STYLE_AIRBUS = 1;

    public static final int STYLE_CLOCK_ANALOG = 0;
    public static final int STYLE_CLOCK_DIGITAL = 1;
    
    public static final int HSI_SOURCE_NAV1 = 0;
    public static final int HSI_SOURCE_NAV2 = 1;
    public static final int HSI_SOURCE_GPS = 2;

    public static final int EFIS_RADIO_ADF = 0;
    public static final int EFIS_RADIO_OFF = 1;
    public static final int EFIS_RADIO_NAV = 2;
    public static final int EFIS_RADIO_BOTH = 999; // for later, only available in override

    public static final int EFIS_MAP_CENTERED = 0;
    public static final int EFIS_MAP_EXPANDED = 1;
    public static final int EFIS_MAP_APP = 0;
    public static final int EFIS_MAP_VOR = 1;
    public static final int EFIS_MAP_MAP = 2;
    public static final int EFIS_MAP_NAV = 3;
    public static final int EFIS_MAP_PLN = 4;

    public static final int EFIS_MAP_RANGE[] = {10, 20, 40, 80, 160, 320, 640};
    public static final int X737_MAP_RANGE[] = {5, 10, 20, 40, 80, 160, 320, 640};

    public static final int XPDR_OFF = 0;
    public static final int XPDR_STBY = 1;
    public static final int XPDR_ON = 2;
    public static final int XPDR_TA = 3;
    public static final int XPDR_TARA = 4;

    public static final int CLOCK_MODE_UTC = 0;
    public static final int CLOCK_MODE_LT = 1;
    public static final int CHR_CONTROL_START_STOP_RESET = 0;
    public static final int CHR_CONTROL_START_STOP = 1;
    public static final int CHR_CONTROL_RESET = 2;

    public static final int CLOCK_ET_STOP = 0;
    public static final int CLOCK_ET_RUN = 1;
    public static final int CLOCK_ET_RESET = 2;
    
    public static final int CLOCK_SRC_GPS = 0;
    public static final int CLOCK_SRC_INT = 1;
    public static final int CLOCK_SRC_SET = 2;
   
    public static final int MFD_MODE_ARPT = 0;
    public static final int MFD_MODE_FPLN = 1;
    public static final int MFD_MODE_RTU = 2;
    public static final int MFD_MODE_EICAS = 3; // MFD_MODE_ENGINE
    public static final int MFD_MODE_ENGINE = 3; // MFD_MODE_EICAS
    public static final int MFD_MODE_BLEED = 4;
    public static final int MFD_MODE_CAB_PRESS = 5;
    public static final int MFD_MODE_ELEC = 6;
    public static final int MFD_MODE_HYDR = 7;
    public static final int MFD_MODE_FUEL = 8;
    public static final int MFD_MODE_APU = 9;
    public static final int MFD_MODE_COND = 10;
    public static final int MFD_MODE_DOOR_OXY = 11;
    public static final int MFD_MODE_WHEELS = 12;
    public static final int MFD_MODE_FCTL = 13;
    public static final int MFD_MODE_SYS = 14; // SYS on Boeing, CRUIZE on Airbus
    public static final int MFD_MODE_STATUS = 15;
    public static final int MFD_MODE_DEBUG_INFORMATION = 31;
    
    public static final int CDU_SOURCE_AIRCRAFT_OR_DUMMY = 0;
    public static final int CDU_SOURCE_XFMC = 1;
    public static final int CDU_SOURCE_UFMC = 2;
    public static final int CDU_LEFT = 0;
    public static final int CDU_RIGHT = 1;
    public static final int CDU_BOTH = 2;

    public static final int RADIO_NAV1 = 1;
    public static final int RADIO_NAV2 = 2;
    public static final int RADIO_ADF1 = 3;
    public static final int RADIO_ADF2 = 4;
    public static final int RADIO_COM1 = 5;
    public static final int RADIO_COM1_833 = 105;
    public static final int RADIO_COM2 = 6;
    public static final int RADIO_COM2_833 = 106;
    public static final int RADIO_NAV1_STDBY = -1;
    public static final int RADIO_NAV2_STDBY = -2;
    public static final int RADIO_ADF1_STDBY = -3;
    public static final int RADIO_ADF2_STDBY = -4;
    public static final int RADIO_COM1_STDBY = -5;
    public static final int RADIO_COM1_STDBY_833 = -105;
    public static final int RADIO_COM2_STDBY = -6;
    public static final int RADIO_COM2_STDBY_833 = -106;

    /**
     * Chronograph action on main, ND1 (CAPT) and ND2 (FO) 
     */
    public static final int CHR_ACT_MAIN_START_RESET = 0;
    public static final int CHR_ACT_MAIN_START_STOP = 1;
    public static final int CHR_ACT_MAIN_RESET = 2;
    public static final int CHR_ACT_CAPT_START_RESET = 3;
    public static final int CHR_ACT_CAPT_START_STOP = 4;
    public static final int CHR_ACT_CAPT_RESET = 5;
    public static final int CHR_ACT_FO_START_RESET = 6;
    public static final int CHR_ACT_FO_START_STOP = 7;
    public static final int CHR_ACT_FO_RESET = 8;

    /**
     * EGPWS Alert messages from X-RAAS
     */
    public static final int EGPWS_ALERT_FLAPS = 1;
    public static final int EGPWS_ALERT_TOO_HIGH = 2;
    public static final int EGPWS_ALERT_TOO_FAST = 3;
    public static final int EGPWS_ALERT_UNSTABLE = 4;
    public static final int EGPWS_ALERT_TWY = 5;
    public static final int EGPWS_ALERT_SHORT_RWY = 6;
    public static final int EGPWS_ALERT_ALTM_SETTING = 7;
    public static final int EGPWS_ALERT_APP = 8;
    public static final int EGPWS_ALERT_ON = 9;
    public static final int EGPWS_ALERT_LONG_LAND = 10;
    public static final int EGPWS_ALERT_DEEP_LAND = 11;
    
    public enum EPGWSAlertLevel { NORMAL, CAUTION, WARNING, NONE };
    
    /**
     * Weather radar mode selector 
     */
    public static final int WXR_MODE_OFF = 0;
    public static final int WXR_MODE_WXR = 1;
    public static final int WXR_MODE_TURB = 2;
    public static final int WXR_MODE_MAP = 3;
    public static final int WXR_MODE_FORCE_ON = 4;
    
    /**
     * Fire Agents
     * TODO: do not limit to twin engine A/C
     */
    public static final int FIRE_AGENT_ENG1_1 = 1;
    public static final int FIRE_AGENT_ENG1_2 = 2;
    public static final int FIRE_AGENT_ENG2_1 = 3;
    public static final int FIRE_AGENT_ENG2_2 = 4;
    public static final int FIRE_AGENT_APU = 5;

    /**
     * Fire Status
     * TODO: do not limit to twin engine A/C
     */
    public static final int FIRE_STATUS_ENG_1 = 1;
    public static final int FIRE_STATUS_ENG_2 = 2;
    public static final int FIRE_STATUS_APU = 3;
    
    /**
     * Flight control computers (Airbus A320)
     * - ELAC: Elevator Aileron Computers
     * - SEC: Spoiler Elevator Computers
     * - FAC: Flight Augmentation Computers
     * Flight control computers (Airbus A330/340)
     * - FCPC: flight control primary computers
     * - FCSC: flight control secondary computers
     * Flight control computers (Airbus A350/380)
     * - PRIM: Primary Flight Control and Guidance Computers
     * - SEC: Secondary Flight Control Computers
     * - SFCC: Slats & Flaps Control Computers
     * Flight control computers (Boeing 777)
     * - ACE: Actuator Control Electronics
     * - PFC: Primary Flight Computers
     */
    public enum FCC {
        ELAC1, ELAC2, SEC1, SEC2, SEC3, FAC1, FAC2
    }
    
    /**
     * @return int - general instrument style STYLE_BOEING, STYLE_AIRBUS
     */
    public int get_instrument_style();
    
    /**
     * @return int - X-Plane 11 FMS type. 
     * 0: X-Plane 11 FMS is not running
     * 1: Boeing 737-800
     * 2-99: Reserved
     */
    public int get_fms_type();  
    
    /**
     * @return int - general instrument style 0=ANALOG, 1=DIGITAL
     */
    public int get_clock_style();
    
    /**
     * @return int - general instrument side : PILOT, COPILOT, INSTRUCTOR
     */
    public InstrumentSide get_instrument_side();

    /**
     * @return boolean - do we have avionics power?
     */
    public boolean power();

    /**
     * @return boolean - Display Unit Power
     */
    public boolean du_power(DU display_unit);
    public boolean du_power(DU display_unit, InstrumentSide side);      
  
    /**
     * @return float - Display Unit Brightness [range from 0.0f to 1.0f]
     */
    public float du_brightness(DU display_unit);
    public float du_brightness(DU display_unit, InstrumentSide side);      
    
    /**
     * @return boolean - is OM lit?
     */
    public boolean outer_marker();

    /**
     * @return boolean - is MM lit?
     */
    public boolean middle_marker();

    /**
     * @return boolean - is IM lit?
     */
    public boolean inner_marker();

    /**
     * @return int - selected range of map display in switch setting
     */
    public int map_range_index();
    public int map_range_index(InstrumentSide side);

    
    /**
     * @return int - selected range of map display in NM
     */
    public int map_range();
    public int map_range(InstrumentSide side);

    /**
     * @return boolean - map is close-up
     */
    public boolean map_zoomin();
    public boolean map_zoomin(InstrumentSide side);

    /**
     * @return int - map mode CENTERED or EXPANDED
     */
    public int map_mode();
    public int map_mode(InstrumentSide side);

    /**
     * @return int - map submode APP, VOR, MAP, NAV or PLN
     */
    public int map_submode();
    public int map_submode(InstrumentSide side);
    
    /**
     * @return int - HSI source selector - either HSI_SOURCE_NAV1, HSI_SOURCE_NAV2 or HSI_SOURCE_GPS
     */
    public int hsi_source();

    /**
     * @return int - EFIS Radio1 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio1();
    public int efis_radio1(InstrumentSide side);

    /**
     * @return int - EFIS Radio2 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio2();
    public int efis_radio2(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays constraints, false otherwise
     */
    public boolean efis_shows_cstr();
    public boolean efis_shows_cstr(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays waypoints, false otherwise
     */
    public boolean efis_shows_wpt();
    public boolean efis_shows_wpt(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays VORs, false otherwise
     */
    public boolean efis_shows_vor();
    public boolean efis_shows_vor(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays NDBs, false otherwise
     */
    public boolean efis_shows_ndb();
    public boolean efis_shows_ndb(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays airports, false otherwise
     */
    public boolean efis_shows_arpt();
    public boolean efis_shows_arpt(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays TCAS information, false otherwise
     */
    public boolean efis_shows_tfc();
    public boolean efis_shows_tfc(InstrumentSide side);

    /**
     * @return boolean - true if EFIS displays FMS altitude information, false otherwise
     */
    public boolean efis_shows_data();

    /**
     * @return boolean - true if EFIS displays bearing lines, false otherwise
     */
    public boolean efis_shows_pos();

    /**
     * @return boolean - true if EFIS displays EGPWS Terrain, false otherwise
     */
    public boolean efis_shows_terrain();
    public boolean efis_shows_terrain(InstrumentSide side);

    /**   
     * @return boolean - true if EFIS displays Vertical Path, false otherwise
     */
    public boolean efis_shows_vp();
    public boolean efis_shows_vp(InstrumentSide side);
   
    /**   
     * @return boolean - true if EFIS displays Weather radar, false otherwise
     */
    public boolean efis_shows_wxr();
    public boolean efis_shows_wxr(InstrumentSide side);
     
    /**
     * @return float - EFIS Chronometer elapsed time (used for the ND)
     */
    public float efis_chrono_elapsed_time();
    public float efis_chrono_elapsed_time(InstrumentSide side);
      
    
    /**
     * @return int - Clock date day
     */
    public int clock_date_day();
    
    /**
     * @return int - Clock date month
     */
    public int clock_date_month();

    /**
     * @return int - Clock date year
     */
    public int clock_date_year();
    
    /**
     * @return boolean - True is clock shows date
     */
    public boolean clock_date_show();

    /**
     * @return int - utc source (0=GPS, 1=INT, 2=SET)
     */
    public int clock_utc_source();
    
    /**
     * @return int - Clock elapsed time selector
     */
    public int clock_et_selector();
    
    /*
     * Weather radar section
     */
    
    /**   
     * @return float - weather radar gain range [0.0f - 1.0f]
     */
    public float wxr_gain();
    public float wxr_gain(InstrumentSide side);
   
    /**   
     * @return float - weather radar tilt range [-15.0° - +15.0°]
     */
    public float wxr_tilt();
    public float wxr_tilt(InstrumentSide side);
    
    /**   
     * @return boolean - weather radar automatic gain adjust
     */     
    public boolean wxr_auto_gain();
    
    /**   
     * @return boolean - weather radar automatic tilt adjust
     */     
    public boolean wxr_auto_tilt();
    
    /**   
     * @return float - weather radar automatic tilt value [-15.0° - +15.0°]
     * Altitude in feet
     * Range in nm
     */     
    public float wxr_auto_tilt_value(float altitude, float range);   
    
    /**   
     * @return boolean - weather radar test
     */     
    public boolean wxr_test();

    /**   
     * @return boolean - weather active 
     * Active when on and not on ground, or when forced on (mode=4)
     */  
    public boolean wxr_active();
    
    /**   
     * @return int - weather radar mode [0=OFF ; 1=weather ; 2=weather+turbulence ; 3=map ; 4=weather(no Standby) ]
     */     
    public int wxr_mode();
    
    /**   
     * @return boolean - weather radar slave settings
     */     
    public boolean wxr_slave();
    
    /**   
     * @return boolean - weather radar REACT
     * Rain Echo Attenuation Compensation Technique
     */        
    public boolean wxr_react();
    
    /**   
     * @return boolean - weather radar narrow mode (60° instead of 120° sweep range)
     */        
    public boolean wxr_narrow();
    
    /**   
     * @return boolean - weather radar alert mode
     */    
    public boolean wxr_alert();
    
    /**   
     * @return boolean - weather radar target mode
     */    
    public boolean wxr_target();
    
    /**
     * @return NavigationRadio - model class representing the currently selected radio or null, if none is selected
     */
    public NavigationRadio get_selected_radio(int bank);

    /**
     * @return NavigationRadio - model class representing the NAV radio
     */
    public NavigationRadio get_nav_radio(int bank);

    /**
     * @return int - adf frequency
     */
    public float get_radio_freq(int radio_num);

    /**
     * @return NavigationRadio - model class representing the GPS
     */
    public NavigationRadio get_gps_radio();

    /**
     * @return Localizer - model class representing the currently selected localizer or null, if none is selected
     */
    public Localizer get_tuned_localizer(int bank);

    /**
     * @return VOR - model class representing the currently selected VOR or null, if none is selected
     */
    public RadioNavBeacon get_tuned_navaid(int bank);

    /**
     * @return float - selected OBS for NAV1 in degrees
     */
    public float nav1_obs();

    /**
     * @return float - selected OBS for NAV2 in degrees
     */
    public float nav2_obs();

    /**
     * @return float - selected course for NAV1 in degrees
     */
    public float nav1_course();

    /**
     * @return float - selected course for NAV2 in degrees
     */
    public float nav2_course();

    /**
     * @return float - selected course for GPS in degrees
     */
    public float gps_course();

    /**
     * @return float - deflection for NAV1 in dots
     */
    public float nav1_hdef_dot();

    /**
     * @return float - deflection for NAV2 in dots
     */
    public float nav2_hdef_dot();

    /**
     * @return float - deflection for GPS in dots
     */
    public float gps_hdef_dot();

    /**
     * @return float - NAV1 OFF/TO/FROM indicator
     */
    public int nav1_fromto();

    /**
     * @return float - NAV2 OFF/TO/FROM indicator
     */
    public int nav2_fromto();

    /**
     * @return float - GPS OFF/TO/FROM indicator
     */
    public int gps_fromto();

    /**
     * @return float - deflection for NAV1 GS in dots
     */
    public float nav1_vdef_dot();

    /**
     * @return float - deflection for NAV2 GS in dots
     */
    public float nav2_vdef_dot();

    /**
     * @return float - deflection for GPS GS in dots
     */
    public float gps_vdef_dot();

    /**
     * @return boolean - NAV1 GS active
     */

    public boolean nav1_gs_active();
    /**
     * @return boolean - NAV2 GS active
     */
    public boolean nav2_gs_active();

    /**
     * @return boolean - GPS GS active
     */
    public boolean gps_gs_active();

    /**
     * @return String - GPS active waypoint ID
     */
    public String gps_nav_id();


    /**
     * @return boolean - COM1 is tuned to a 8.33kHz spaced channel
     */
    public boolean com1_is_833();

    
    /**
     * @return boolean - COM1 STANDBY is tuned to a 8.33kHz spaced channel
     */
    public boolean com1_standby_is_833();

    
    /**
     * @return boolean - COM2 is tuned to a 8.33kHz spaced channel
     */
    public boolean com2_is_833();

    
    /**
     * @return boolean - COM2 STANDBY is tuned to a 8.33kHz spaced channel
     */
    public boolean com2_standby_is_833();

    
    /**
     * @return boolean - Contacting ATC
     */
    public boolean contact_atc();

    /**
     * @return boolean - Receiving from PilotEdge
     */
    public boolean pilotedge_rx();
    
    
    /**
     * @return int - Selected radio on the RTU
     */
    public int rtu_selected_radio();

    
    /**
     * @return int - MFD mode (0=arpt, 1=fpln, 2=eicas)
     */
    public int get_mfd_mode();

    
    /**
     * @return int - CDU source (0=aircraft's custom FMC or dummy, 1=X-FMC, 2=UFMCS/X737FMC,)
     */
    public int get_cdu_source();
    
    /**
     * @return int - CDU side (0=Captain Left CDU, 1=First Officer Right CDU, 2=Both CDU)
     */
    public int get_cdu_side();
    
    /**
     * @return int - 0=LbFt, 1=Nm, 2=percent
     */
    public int get_trq_scale();
    
    
    /**
     * @return int - 0=Kg, 1=Lb, 2=USG, 3=Liter
     */
    public int get_fuel_units();
    
    /**
     * @return float 
     */
    public float fuel_multiplier();

    /**
     * @return int - 0=Celcius, 1=Farhenheit, 2=Kelvin
     */
    public int get_temp_units();
  
    /**
     * @return float / if temp_units in fahrenheit : T(°F) = 1,8 T(°C) + 32
     */
    public float convert_temperature(float temp_in_celcius);
    
    /**
     * @return int - Engine type (0=N1, 1=EPR, 2=TRQ, 3=MAP)
     */
    public int get_engine_type();

    /**
     * TODO: constants for autopilot states need to be defined
     * 
     * @return int - bitmask for autopilot state
     */
    public int autopilot_state();

    /**
     * @return float - vertical velocity in feet per minute selected in autopilot
     */
    public float autopilot_vv();

    /**
     * @return float - autopilot altitude preselect
     */
    public float autopilot_altitude();

    /**
     * @return float - autopilot speed
     */
    public float autopilot_speed();

    /**
     * @return boolean - autopilot speed is mach
     */
    public boolean autopilot_speed_is_mach();

    /**
     * @return boolean - autopilot autoland warning indicator
     */
    public boolean autopilot_autoland_warning();
    
    /**
     * @return float - autopilot bank angle in degrees
     * default value is 25°
     * this value is used by the FMC to compute the turn radius
     */
    public float autopilot_bank_angle();
    
    /**
     * @return float - heading in degrees selected in autopilot
     */
    public float heading_bug();        // degrees

    /**
     * @return float - FD pitch command
     */
    public float fd_pitch();

    /**
     * @return float - ACF pitch at the same transmit frequency as the FD data
     */
    public float acf_pitch();

    /**
     * @return float - FD roll command
     */
    public float fd_roll();

    /**
     * @return float - ACF bank at the same transmit frequency as the FD data
     */
    public float acf_bank();

    /**
     * @return integer - FWC (Flight Warning Computer) flight phase
     * 0 : power off
     * 1 : electric power on
     * 2 : 1st engine started
     * 3 : 1st engine to TOGA/FLEX power
     * 4 : 80 kts
     * 5 : Lift off
     * 6 : 1500 ft radio or 2mn after lift off - cruise period
     * 7 : 800 ft radio
     * 8 : Touch down
     * 9 : 80 kts
     * 10 : 2nd engine shutdown
     * 0 or 1 : 5 mn after phase 10
     * 
     */
    public int fwc_phase();
    
    /**
     * @return boolean - Takeoff Alarm Inhibits
     * 
     */
    public boolean to_inhibit();
    
    /**
     * @return boolean - Landing Alarm Inhibits
     * 
     */
    public boolean ldg_inhibit();
    
    /**
     * @return boolean - receiving X737 data
     */
    public boolean is_x737();

    public int x737_mcp_spd();

    public int x737_fmc_spd();

    public int x737_retard();

    public int x737_thr_hld();

    public int x737_lnav_armed();

    public int x737_vorloc_armed();

    public int x737_pitch_spd();

    public int x737_alt_hld();

    public int x737_vs_armed();

    public int x737_vs();

    public int x737_vnav_alt();

    public int x737_vnav_path();

    public int x737_vnav_spd();

    public int x737_gs_armed();

    public int x737_gs();

    public int x737_flare_armed();

    public int x737_flare();

    public int x737_toga();

    public int x737_lnav();

    public int x737_hdg();

    public int x737_vorloc();

    public int x737_n1();

    public boolean x737_athr_armed();

    /**
     * @return boolean - receiving ZiboMod Boeing 737 data
     */
    public boolean is_zibo_mod_737();

    /**
     * @return int - autopilot mode (0=Off/1=FD/2=CMD)
     */
    public int autopilot_mode();

    public boolean autothrottle_enabled();

    public boolean autothrottle_on();

    public boolean ap_hdg_sel_on();

    public boolean ap_vorloc_arm();

    public boolean ap_vorloc_on();

    public boolean ap_lnav_arm();

    public boolean ap_lnav_on();

    public boolean ap_vs_arm();

    public boolean ap_vs_on();

    public boolean ap_flch_on();

    public boolean ap_alt_hold_arm();

    public boolean ap_alt_hold_on();

    public boolean ap_gs_arm();

    public boolean ap_gs_on();

    public boolean ap_bc_arm();

    public boolean ap_bc_on();

    public boolean ap_vnav_arm();

    public boolean ap_vnav_on();

    public boolean ap_vtoga_arm();

    public boolean ap_vtoga_on();

    public boolean ap_ltoga_arm();

    public boolean ap_ltoga_on();

    public boolean ap_roll_on();

    public boolean ap_pitch_on();
    
    /**
     * @return Aircraft - reference to aircraft model class to which avionics belongs
     */
    public Aircraft get_aircraft();

    /**
     * @return FMS - reference to flight management system model class
     */
    public FMS get_fms();

    /**
     * @return TCAS - reference to tcas model class
     */
    public TCAS get_tcas();
   
    /**
     * @return int - transponder mode
     */
    public int transponder_mode();

    /**
     * @return int - transponder code
     */
    public int transponder_code();

    /**
     * @return boolean - Transponder IDENT
     */
    public boolean transponder_ident();

    
    /**
     * @return boolean - clock show UTC
     */
    public boolean clock_shows_utc();
    
    
    /**
     * @return boolean - true if metric altitude is shown on PFD
     */
    public boolean pfd_shows_metric_alt();
 
    /**
     * @return boolean - true if metric speed is shown on PFD
     */
    public boolean pfd_shows_metric_speed();
    
    /**
     * @return boolean - true if ILS is displayed on PFD (but may not receiving any signal)
     */
    public boolean pfd_shows_ils();
    public boolean pfd_shows_ils(InstrumentSide side);
    
    /**
     * @return boolean - true if baro is in HPa, false if baro is in Hg
     */
    public boolean pfd_shows_baro_hpa();
    public boolean pfd_shows_baro_hpa(InstrumentSide side);
    	   
    
    /**
     * @return boolean - receiving CL30 data
     */
    public boolean is_cl30();
    
    /**
     * @return int - CL30's sim/custom/xap/refspds
     */
    public int cl30_refspds();

    public int cl30_v1();
    public int cl30_vr();
    public int cl30_v2();
    public int cl30_vt();
    public int cl30_vga();
    public int cl30_vref();
    public float cl30_mast_warn();
    public float cl30_mast_caut();

    // QPAC Airbus Logic
    public boolean is_qpac();
    public int qpac_version();
    
    // Autopilot
    /**
     * @return boolean - QPAC Autopilot 1 or 2 engaged
     */    
    public boolean qpac_ap1();
    public boolean qpac_ap2();
    
    /**
     * @return int - QPAC Autopilot phase
     */    
    public int qpac_ap_phase();
    
    public int qpac_presel_crz();
    public int qpac_presel_clb();
    public float qpac_presel_mach();
  
    public int qpac_ap_vertical_mode();
    public int qpac_ap_vertical_armed();

    public int qpac_ap_lateral_mode();
    public int qpac_ap_lateral_armed();
    
    public int qpac_npa_valid();
    public int qpac_npa_no_points();
    public boolean qpac_loc_illuminated();
    public boolean qpac_appr_illuminated();
    public int qpac_appr_type();
    public float qpac_appr_mda();
    public boolean qpac_alt_is_cstr();
    public int qpac_constraint_alt();
    // FCU
    public boolean qpac_fcu_hdg_trk();
    public boolean qpac_fcu_metric_alt();
    public boolean qpac_fcu_vs_dashed();
    public boolean qpac_fcu_spd_dashed();
    public boolean qpac_fcu_spd_managed();
    public boolean qpac_fcu_hdg_dashed();
    public boolean qpac_fcu_hdg_managed();
    public boolean qpac_fcu_alt_managed();    
    // Auto-Thrust
    public int qpac_athr_mode();
    public int qpac_athr_mode2();
    public int qpac_athr_limited();
    public int qpac_thr_lever_mode();
    public int qpac_fma_thr_warning();
    public int qpac_flex_temp();
    
    // ILS Sig and Deviation Capt. and FO
    public boolean qpac_ils_on();
    public boolean qpac_ils_on(InstrumentSide side);
    public float qpac_loc_val();
    public boolean qpac_loc_on();
    public float qpac_gs_val();
    public boolean qpac_gs_on();
    public float qpac_ils_crs();
    public float qpac_ils_freq();
    public String qpac_ils_id();
    public float qpac_ils_dme();
    
    // FD
    /**
     * @return boolean - QPAC Flight director 1 or 2 engaged
     */  
    public boolean qpac_fd1();
    public boolean qpac_fd2();
    public boolean qpac_fd_on();
    public float qpac_fd1_ver_bar();
    public float qpac_fd1_hor_bar();
    public float qpac_fd1_yaw_bar();
    public float qpac_fd2_ver_bar();
    public float qpac_fd2_hor_bar();
    public float qpac_fd2_yaw_bar();
    public float qpac_fd_ver_bar();
    public float qpac_fd_hor_bar();
    public float qpac_fd_yaw_bar();
    
    // Baro
    public boolean qpac_baro_std();
    public boolean qpac_baro_std(InstrumentSide side);
    public boolean qpac_baro_unit();
    public boolean qpac_baro_unit(InstrumentSide side);
    public boolean qpac_baro_hide();
      
    // V Speeds
    public float qpac_v1_value();
    public int qpac_v1();
    public int qpac_vr();
    public int qpac_vmo();
    public int qpac_vls();  
    public int qpac_vf();
    public int qpac_vs();
    public int qpac_v_green_dot();
    public int qpac_alpha_prot();
    public int qpac_alpha_max();
    public int qpac_vfe_next();
    
    // Failures
    public float qpac_failures();
    // Flight controls computers
    /**
     * @return boolean - True is FFC #pos is active ; False if failed
     */    
    public boolean qpac_fcc(int pos);
    
    /**
     * @return boolean - True is FFC #pos is active ; False if failed
     */
    public boolean qpac_fcc(FCC pos);

    /**
     * Returns true if the specified FCC is on, false if it is off, false if no
     * airbus is active.
     *
     * @param pos The FCC the information is requested for
     * @return true if the specified FCC is on, false if it is off, false if no
     * airbus is active.
     */
    public boolean fcc(FCC pos);

    /**
     * Returns true if the currently selected aircraft is either the QPAC A320,
     * the FF A320 or the JAR A320 Neo.
     *
     * @return
     */
    public boolean is_airbus();

    // Flight Factor A320
    public boolean is_ff_a320();

    public boolean ff_a320_fcc_off(FCC flightComputer);

    public boolean ff_a320_fcc_fault(FCC flightComputer);
    
    // JARDesign A320 Neo
    public boolean is_jar_a320neo();
    // Baro
    public boolean jar_a320neo_baro_unit();
    // Auto-pilot
    public boolean jar_a320neo_ap1();
    public boolean jar_a320neo_ap2();
    public boolean jar_a320neo_fd();
    public int jar_a320neo_ap_phase();
    public int jar_a320neo_ap_common_mode();
    public int jar_a320neo_ap_vertical_mode();
    public int jar_a320neo_ap_vertical_armed();
    public int jar_a320neo_ap_lateral_mode();
    public int jar_a320neo_ap_lateral_armed();
    // Approach
    public boolean jar_a320neo_appr_illuminated();
    public boolean jar_a320neo_loc_illuminated();
    public float jar_a320neo_appr_mda();
    public float jar_a320neo_appr_dh();
    // FCU
    public boolean jar_a320neo_fcu_hdg_trk();
    public boolean jar_a320neo_fcu_metric_alt();
    public boolean jar_a320neo_fcu_vs_dashed();
    public boolean jar_a320neo_fcu_spd_dashed();
    public boolean jar_a320neo_fcu_spd_managed();
    public boolean jar_a320neo_fcu_hdg_dashed();
    public boolean jar_a320neo_fcu_hdg_managed();
    public boolean jar_a320neo_fcu_alt_managed();    
    // Auto-Thrust
    public int jar_a320neo_athr_mode();
    public int jar_a320neo_thr_mode();
    public int jar_a320neo_athr_limited();
    public int jar_a320neo_thr_lever_mode();
    public int jar_a320neo_fma_thr_warning();
    public int jar_a320neo_flex_temp();   
    // V-Speeds
    public int jar_a320neo_v1();
    public int jar_a320neo_vr();
    public int jar_a320neo_vmo();
    public int jar_a320neo_vls();
    public int jar_a320neo_vs();
    public int jar_a320neo_vf();
    public int jar_a320neo_v_green_dot();
    public int jar_a320neo_alpha_prot();
    public int jar_a320neo_alpha_max();
    
        
    /*
     *  EGPWS functions
     *  Runway Awareness and Advisory System
     *  Runway Overrun Prevention System [TODO]
     */
    public EPGWSAlertLevel egpws_alert_level();
    public String egpws_alert_message();
    
    
    // UFMC
    public boolean has_ufmc();

    public float ufmc_v1();
    public float ufmc_vr();
    public float ufmc_v2();
    public float ufmc_vref();
    public float ufmc_vf30();
    public float ufmc_vf40();


    //    public void set_power(boolean new_power);

    public void set_instrument_style(int new_style);

    public void set_hsi_source(int new_source);

    public void set_nav1_obs(float new_obs1);

    public void set_nav2_obs(float new_obs2);

    public void set_radio1(int new_radio1);

    public void set_radio2(int new_radio2);

    public void set_zoomin(boolean new_closeup);

    public void set_submode(int new_submode);

    public void set_mode(int new_mode);

    public void set_trq_scale(int new_scale);

    public void set_max_trq_override(float new_max_trq);

    public void set_fuel_units(int new_units);
    
    public void set_temp_units(int new_units);

    public void set_engine_type(int new_type);
    
    public void set_range_index(int new_range_index);

    public void set_xpdr_mode(int new_xpdr_mode);

    public void set_clock_mode(int new_clock_mode);

    public void chr_control(int chr_action);
    
    /**
     * Send show date command
     */
    public void set_clock_show_date(boolean show_date);

    /**
     * utc_source: int (0=GPS, 1=INT, 2=SET)
     */
    public void set_clock_utc_source(int utc_source);
    
    /**
     * elapsed time selector : 0=STOP, 1=RUNNING, 2=RESET
     */
    public void set_clock_et_selector(int et_selector);
    
    
    public void set_show_cstr(boolean new_cstr);

    public void set_show_arpt(boolean new_arpt);

    public void set_show_wpt(boolean new_wpt);

    public void set_show_vor(boolean new_vor);

    public void set_show_ndb(boolean new_ndb);

    public void set_show_tfc(boolean new_tfc);

    public void set_show_pos(boolean new_pos);

    public void set_show_data(boolean new_data);
    
    public void set_show_terrain(boolean new_terrain);
    public void set_show_terrain(boolean new_data, InstrumentSide side);
    
    public void set_show_vp(boolean new_vp);
    
    public void set_metric_alt(boolean new_metric_alt);
    
    public void set_ils(boolean new_ils);
    public void set_ils(boolean new_ils, InstrumentSide side);
    
    public void set_track_fpa(boolean new_track_fpa);
    
    /*
     * Weather radar
     */
    
    public void set_show_wxr(boolean new_wxr);
     
    public void set_wxr_gain(float new_gain);
    
    public void set_wxr_tilt(float new_tilt);
       
    public void set_wxr_auto_tilt(boolean new_auto_tilt);
    
    public void set_wxr_auto_gain(boolean new_auto_gain);

    public void set_wxr_test(boolean new_test);
    
    public void set_wxr_mode(int new_mode);

    public void set_wxr_slave(boolean new_slave);
    
    public void set_wxr_react(boolean new_react);
    
    public void set_wxr_narrow(boolean new_narrow);
    
    public void set_wxr_alert(boolean new_alert);
    
    public void set_wxr_target(boolean new_target);
    
    /*
     *  Autopilot 
     */
    
    public void set_autopilot_altitude(float new_altitude);
    
    public void set_autopilot_speed(float new_speed);
    
    public void set_autopilot_vv(float new_vertical_speed);
    
    public void set_autopilot_hdg(float new_heading);
    
    public void set_autopilot_mode(int new_mode);
    
    
    public void set_mfd_mode(int new_mode);

    public void set_cdu_source(int new_source);
    
    /**
     * Set CDU send and display side 
     * @param new_side CDU_LEFT=0 ; CDU_RIGHT=1
     */
    public void set_cdu_side(int new_side);

    // Failures
    public enum FailedElement { PFD_ATTITUDE, PFD_ALTITUDE, PFD_AOA, PFD_AIR_SPEED, PFD_HEADING, PFD_TURN, PFD_VSI };
    public enum FailureMode { WORKING, FAIL_MEAN_TIME, FAIL_TIME, FAIL_SPD, FAIL_ALT, FAIL_KEY, INOPERATIVE  };
  
    public FailureMode failure_mode(FailedElement element);
    public void set_failure(FailedElement element, FailureMode mode);    
    
    /**
     * @return boolean - True if attitude valid
     */  
    public boolean att_valid ();

    /**
     * @return boolean - True if heading valid
     */  
    public boolean hdg_valid ();

    /**
     * @return boolean - True if IAS valid 
     */  
    public boolean ias_valid ();

    /**
     * @return boolean - True if Altitude valid 
     */  
    public boolean alt_valid ();
    

}
