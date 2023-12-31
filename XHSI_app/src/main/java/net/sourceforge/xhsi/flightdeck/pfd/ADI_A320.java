/**
* ADI_A320.java
* 
* This is the Airbus A320 family version of ADI.java Attitude & Director Indicator
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014,2022  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40 page 1 REV 36
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

// import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences.DrawYokeInputMode;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.util.FramedElement.FE_Color;
import net.sourceforge.xhsi.util.FramedElement.FE_FontSize;
import net.sourceforge.xhsi.model.Aircraft.StickPriorityMessage;


public class ADI_A320 extends PFDSubcomponent {

	private static final long serialVersionUID = 1L;

	// private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	// Set of enums and private data to manage FD bar flashing
	enum FDEngagement { NONE, FD, AP, FD_AP };
	enum FDReversion { NORMAL, VS };
	enum FDLand { NONE, LAND, LAND_ON_LOC, LAND_ON_GS };
	enum FDAltCapture { NONE, ALT, ALT_STAR };
	private boolean fd_flashing = false;
	private long fd_flashing_start = 0;
	// private int ap_alt = 0; 
	private FDEngagement fd_engagement = FDEngagement.NONE;
	private FDReversion fd_reversion = FDReversion.NORMAL;
	// private FDLand fd_land = FDLand.NONE;
	// private FDAltCapture fd_alt_capture = FDAltCapture.NONE;

    PFDFramedElement failed_flag;
    PFDFramedElement stick_priority_box;

	public ADI_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
        failed_flag = new PFDFramedElement(PFDFramedElement.ATT_FLAG, 0, hsi_gc, FE_Color.ALARM);
        failed_flag.enableFlashing();
        failed_flag.disableFraming();
        failed_flag.setFontSize(FE_FontSize.XXL);
        stick_priority_box = new PFDFramedElement(PFDFramedElement.ATT_FLAG, 0, hsi_gc, FE_Color.ALARM);
        stick_priority_box.enableFlashing();
        stick_priority_box.disableFraming();
        stick_priority_box.setFontSize(FE_FontSize.XXL);
	}


	public void paint(Graphics2D g2) {
		if ( pfd_gc.airbus_style ) {
			if ( ! XHSIStatus.receiving  || ! this.avionics.att_valid() ) {
				// FCOM 1.31.40 p26 (1) 
				// if the PFD loses attitude data, its entire sphere is cleared to display the ATT flag (red)
				if ( pfd_gc.powered ) drawFailedADI(g2);
			} else if ( pfd_gc.powered ) {
				failed_flag.clearText();
				drawADI(g2);
				drawMarker(g2);
				drawStrickPriorityMessage(g2);
			} 
		}       
	}

	private void drawFailedADI(Graphics2D g2) {

		/*
		 * Code for drawing a grey background ADI 		
		 *
		 * g2.setColor(pfd_gc.pfd_instrument_background_color);
		 * g2.draw(pfd_gc.adi_airbus_horizon_area);
		 */
		
    	failed_flag.setText("ATT", FE_Color.ALARM);    	
    	failed_flag.paint(g2);
	}

	private void drawADI(Graphics2D g2) {

		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int right = pfd_gc.adi_size_right;
		int up = pfd_gc.adi_size_up;
		int down = pfd_gc.adi_size_down;
		int p_90 = pfd_gc.adi_pitch90;
		int scale = pfd_gc.adi_pitchscale;
        int ra = Math.round(this.aircraft.agl_m() * 3.28084f); // Radio altitude
        boolean airborne = ! this.aircraft.on_ground();
        boolean protections = this.avionics.is_qpac() || this.avionics.is_jar_a320neo();
        int mark_size = left * 6/10;
        // float alt_f_range = 1100.0f;
        // int gnd_y = pfd_gc.adi_cy + Math.round( (this.aircraft.agl_m() * 3.28084f) * pfd_gc.tape_height / alt_f_range );
        
        // Get engines status : Slideslip Blue target and ground stick orders
		boolean engine_started = false;
		boolean engine_takeoff = false;
		boolean beta_target = false;
		float max_n1=0.0f;
		float min_n1=1000.0f;
		float n1;
		for (int pos=0; pos<this.aircraft.num_engines(); pos++) {
			n1 = this.aircraft.get_N1(pos);
			if (n1 > max_n1) max_n1 = n1; 
			if (n1 < min_n1) min_n1 = n1;
			if (n1 > 5.0f) engine_started = true;
			if (n1 > 80.0f) engine_takeoff = true;
		}
		if (((max_n1 - min_n1) > 35.0f) && engine_takeoff && this.aircraft.get_flap_handle() > 0.0f ) beta_target=true;
        
		boolean colorgradient_horizon = this.preferences.get_draw_colorgradient_horizon();

		float pitch = this.aircraft.pitch(); // radians? no, degrees!
		
		float bank = this.aircraft.bank(); // degrees
		
		// Flight Director Mode (VS/HDG mode)
		boolean fd_on = this.avionics.autopilot_mode() >= 1 ? true : false;	
		boolean path_director_on = false;
		if ( this.avionics.is_qpac()) { 
			fd_on = this.avionics.qpac_fd_on();
			// if (this.avionics.qpac_fd1_hor_bar() == -1.0f) fd_on = false;
			if (this.avionics.qpac_fcu_hdg_trk() && !this.aircraft.on_ground()) { fd_on = false; path_director_on = true; }
		}
		if ( this.avionics.is_jar_a320neo() ) {			
			if (this.avionics.jar_a320neo_fcu_hdg_trk() && !this.aircraft.on_ground()) { fd_on = false; path_director_on = true; }		
		}
		
		// FCOM 1.27.20p3 FD bars are removed when pitch > 25°up or pitch < 13° down restored when pitch between 10° down and 22°up
		// FCOM 1.31.40p3 FD bars are removed when bank > 45° restored when bank < 40°
		if (pitch > 25.0f || pitch < -13.0f || Math.abs(bank) > 45.0f) { fd_on=false; path_director_on=false; }		
		
		// full-scale pitch down = adi_pitchscale (eg: 22°)
		int pitch_y = cy + (int)(down * pitch / scale);

		// Memorize original graphic settings
		Shape original_clipshape = g2.getClip();
		AffineTransform original_at = g2.getTransform();
		Stroke original_stroke = g2.getStroke();
		

		
		if ( ! colorgradient_horizon ) {
			g2.clipRect(cx - left, cy - up, left + right, up + down);
		} else if ( this.preferences.get_draw_fullwidth_horizon() ) {
			if ( pfd_gc.draw_hsi ) {
				g2.clipRect(
						pfd_gc.panel_rect.x,
						pfd_gc.panel_rect.y, 
						pfd_gc.panel_rect.width,
						pfd_gc.dg_cy - pfd_gc.dg_radius - pfd_gc.hsi_tick_w - pfd_gc.line_height_xl*3/2 - pfd_gc.panel_rect.y
						);
			} else {
				g2.clipRect(
						pfd_gc.panel_rect.x, 
						pfd_gc.panel_rect.y + pfd_gc.panel_offset_y, 
						pfd_gc.panel_rect.width, 
						pfd_gc.panel_rect.width
						);
			}
		}

		// g2.rotate(Math.toRadians(-bank), cx, cy);

		int diagonal = colorgradient_horizon ?
				(int)Math.hypot( Math.max(cx, pfd_gc.panel_rect.width - cx), Math.max(cy, pfd_gc.panel_rect.height - cy) ) :
					(int)Math.hypot( Math.max(left, right), Math.max(up, down) );

		int pitch_y_min = cy - up*37/48;
		int pitch_y_max = cy + down * 37/48;
		int pitch_y_airbus = pitch_y;
		if ( colorgradient_horizon ) {
			g2.rotate(Math.toRadians(-bank), cx, cy);
			GradientPaint up_gradient = new GradientPaint(
					cx - diagonal, pitch_y - p_90, pfd_gc.background_color,
					cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
					false);
			GradientPaint sky_gradient = new GradientPaint(
					cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
					cx - diagonal, pitch_y, pfd_gc.brightsky_color,
					false);
			GradientPaint ground_gradient = new GradientPaint(
					cx - diagonal, pitch_y, pfd_gc.brightground_color,
					cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
					false);
			GradientPaint down_gradient = new GradientPaint(
					cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
					cx - diagonal, pitch_y + p_90 , pfd_gc.background_color,
					false);

			g2.setPaint(up_gradient);
			g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90/2 + 2);
			g2.setPaint(sky_gradient);
			g2.fillRect(cx - diagonal, pitch_y - p_90/2, 2 * diagonal, p_90);

			g2.setPaint(ground_gradient);
			g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90/2 + 2);
			g2.setPaint(down_gradient);
			g2.fillRect(cx - diagonal, pitch_y + p_90/2, 2 * diagonal, p_90/2);
			g2.setColor(pfd_gc.markings_color);
			g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);			
		} else { // if (this.preferences.get_draw_airbus_horizon()) {
			// g2.clipRect(cx - left, cy - up, left + right, up + down)
			// With Airbus shape, bank mark area is always blue and RA area is always brown
			g2.setClip(pfd_gc.adi_airbus_horizon_area);
			g2.rotate(Math.toRadians(-bank), cx, cy);
			if (pitch_y > pitch_y_max) pitch_y_airbus = pitch_y_max;
			if (pitch_y < pitch_y_min) pitch_y_airbus = pitch_y_min;
			g2.setColor(pfd_gc.pfd_sky_color);
			g2.fillRect(cx - diagonal, pitch_y_airbus - p_90, 2 * diagonal, p_90);
			g2.setColor(pfd_gc.pfd_ground_color);
			g2.fillRect(cx - diagonal, pitch_y_airbus, 2 * diagonal, p_90);	
			g2.setColor(pfd_gc.pfd_markings_color);
			g2.drawLine(cx - diagonal, pitch_y_airbus, cx + diagonal, pitch_y_airbus);
//		} else {
//			g2.rotate(Math.toRadians(-bank), cx, cy);
//			g2.setColor(pfd_gc.sky_color);
//			g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90);
//			g2.setColor(pfd_gc.ground_color);
//			g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90);
//			g2.setColor(pfd_gc.markings_color);
//			g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);
		}

		g2.setTransform(original_at);


		if ( this.preferences.get_draw_roundedsquare_horizon() ) {
			g2.setColor(pfd_gc.background_color);
			g2.fill(pfd_gc.adi_area);
		}
		
		g2.rotate(Math.toRadians(-bank), cx, cy);
			
		// pitch marks
		// Compute bottom ADI line for clipping
		int bottom_adi_y_max = cy + down*37/48 - 2; // this is the max and the min is cy.
		int bottom_adi_y;
		if (ra < 570) {
			int positive_ra = (ra>0) ? ra : 0;
			bottom_adi_y = pitch_y + ((bottom_adi_y_max - cy) * positive_ra / 180);
			if (bottom_adi_y > bottom_adi_y_max ) { bottom_adi_y = bottom_adi_y_max; }
			Area pitchmark_area = new Area ( new Rectangle(
					cx - left + left/16,
					cy - up*37/48,
					left - left/16 + right - right/16,
					up*37/48 + down*37/48 - (bottom_adi_y_max - bottom_adi_y) 
					) );

			// intersect with the previous clip
			g2.clip( pitchmark_area );
		} else {
			bottom_adi_y = bottom_adi_y_max;
			g2.clip( pfd_gc.adi_pitchmark_area );
		}

		// Top and bottom lines
		// The bottom lines moves up between ground and 120ft AGL.
		g2.setColor(pfd_gc.pfd_markings_color);
		g2.drawLine(cx - left, cy - up*37/48   + 2, cx + right, cy - up*37/48   + 2 );
		g2.drawLine(cx - left, bottom_adi_y, cx + right, bottom_adi_y );
	
		// Heading marks (draw before full clipping)
		float hdg = this.aircraft.heading();
		int hdg10 = (int)Math.round( hdg / 10.0f ) * 10;
        for (int hdg_mark = hdg10 - 30; hdg_mark <= hdg10 + 30; hdg_mark += 10) {
        	int hdg_x = pfd_gc.adi_cx + Math.round( ((float)hdg_mark - hdg) * pfd_gc.hdg_width / 50.0f );
        	g2.drawLine(hdg_x, pitch_y, hdg_x, pitch_y + pfd_gc.adi_size_down/20);
        }
		
		drawPitchmark(g2, pitch, +175, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +150, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +125, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +100, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +75, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +50, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +25, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,    0, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -25, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -50, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -75, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -100, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -125, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -150, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -175, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -200, pitch_y, p_90, cx, cy, mark_size, protections);
		

		// no more clipping ...
		g2.setClip(original_clipshape);
		// ... unless
		if ( ! colorgradient_horizon ) {
			g2.setTransform(original_at);
			g2.clipRect(cx - left, cy - up, left + right, up + down);
			g2.rotate(Math.toRadians(-bank), cx, cy);
		}


		// bank pointer
		int[] bank_pointer_x = {
				cx,
				cx - left/14,
				cx + right/14 };
		int[] bank_pointer_y = {
				cy - up + 1,
				cy - up*29/32 + 1 ,
				cy - up*29/32 + 1 };
		g2.setColor(pfd_gc.pfd_reference_color);
		if ( Math.abs(bank) > 35.0f ) {
			if ( Math.abs(bank) > 70.0f ) {
				g2.setColor(pfd_gc.warning_color);
			} else {
				g2.setColor(pfd_gc.caution_color);
			}
			g2.fillPolygon(bank_pointer_x, bank_pointer_y, 3);
		}
		g2.drawPolygon(bank_pointer_x, bank_pointer_y, 3);

		// slip/skid indicator => Beta target on airbus
		float ss = - this.aircraft.sideslip();
		int ss_x;
		if ( beta_target ) 	g2.setColor(Color.blue);
		if ( Math.abs(ss) > 10.0f ) {
			ss = 10.0f * Math.signum(ss);
			ss_x = cx - Math.round(ss * left/6 / 10.0f);
			g2.fillRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);
		} else {
			ss_x = cx - Math.round(ss * left/6 / 10.0f);
		}
		int[] slip_pointer_x = {
				ss_x + right/12,
				ss_x - left/12,
				ss_x - left/8,
				ss_x + right/8 };
		int[] slip_pointer_y = {
				cy - up*28/32 + 1,
				cy - up*28/32 + 1,
				cy - up*26/32 + 1,
				cy - up*26/32 + 1 };
		g2.drawPolygon(slip_pointer_x, slip_pointer_y, 4);

		// Selected heading when FD off
		if (! fd_on) {
			float hdg_bug = this.avionics.heading_bug() - this.aircraft.heading();
			if ( hdg_bug >  180.0f ) hdg_bug -= 360.0f;
			if ( hdg_bug < -180.0f ) hdg_bug += 360.0f;
			int bug_cx = cx + Math.round( hdg_bug * pfd_gc.hdg_width / 50.0f );
			
			if ((bug_cx > (cx-(left*9/10))) && (bug_cx < (cx+(right*9/10)))) {
				g2.setColor(pfd_gc.pfd_selected_color);
				g2.setStroke(pfd_gc.adi_hdg_bug_stroke);
				g2.drawLine(bug_cx, pitch_y_airbus, bug_cx, pitch_y_airbus - up*3/32);	
				g2.setStroke(original_stroke);
			}
		}
	
		// Display Radar Altitude
		drawRadarAltitude(g2, ra, cx);

		g2.setTransform(original_at);


		// FPV Flight Path Vector or Bird 
		boolean fpv_on = ! this.aircraft.on_ground();	
		if ( this.avionics.is_qpac()) { 
			fpv_on = this.avionics.qpac_fcu_hdg_trk() && ! this.aircraft.on_ground();						
		}
		if ( this.avionics.is_jar_a320neo()) { 
			fpv_on = this.avionics.jar_a320neo_fcu_hdg_trk() && ! this.aircraft.on_ground();						
		}
		if ( fpv_on ) {
			int dx = (int)(down * this.aircraft.drift() / scale);
			int dy = (int)(down * this.aircraft.aoa() / scale);
			if ( (Math.abs(dx) < down) && (Math.abs(dy) < down) ) {
				int fpv_x = cx - dx;
				int fpv_y = cy + dy;
				int fpv_r = down/17;
				g2.setColor(pfd_gc.pfd_active_color);
				g2.setStroke(pfd_gc.adi_fpv_stroke);
				g2.drawOval(fpv_x - fpv_r, fpv_y - fpv_r, fpv_r*2, fpv_r*2);
				g2.drawLine(fpv_x, fpv_y - fpv_r, fpv_x, fpv_y - fpv_r*17/10);
				g2.drawLine(fpv_x - fpv_r, fpv_y, fpv_x - fpv_r*26/10, fpv_y);
				g2.drawLine(fpv_x + fpv_r, fpv_y, fpv_x + fpv_r*26/10, fpv_y);
				g2.setStroke(original_stroke);
			}
		}


		/*
		 *  Aircraft symbol
		 *  TODO: Compute image in graphics context
		 *  aircraft_dimmed = fpv_on && !fd_on;
		 */
		drawAircraftSymbol(g2, fpv_on && !fd_on);
		
		/*
		 * Stick orders - control commands displayed on the PFD inside the ADI
		 * Normal mode: displayed only when the aircraft is on ground and engine started
		 */
		drawStickOrders(g2, ra, airborne, engine_started);
		

		// Controls flight director flashing (FCOM 1.31.40 p18)
		// - reversion to the HDG V/S (manual or automatic)
		// - change of selected speed when ALT* 
		// - loss of LOC or G/S in LAND mode
		// - loss of LAND mode
		// - first AP or FD engagement
		boolean ap_on;
		if (this.avionics.is_qpac()) {
			ap_on = this.avionics.qpac_ap1() || this.avionics.qpac_ap2();
		} else if (this.avionics.is_jar_a320neo()) {
			ap_on = this.avionics.jar_a320neo_ap1() || this.avionics.jar_a320neo_ap2();
		} else {
			ap_on = this.avionics.autopilot_mode() > 1;
		}
		if ( (fd_engagement != FDEngagement.AP) && (fd_engagement != FDEngagement.FD_AP) && ap_on) {
			// Autopilot was engaged
			fd_flashing_start = pfd_gc.current_time_millis;
			fd_flashing = true;
		}
		if ( (fd_engagement != FDEngagement.FD) && (fd_engagement != FDEngagement.FD_AP) && fd_on ) {
			// FD was engaged
			fd_flashing_start = pfd_gc.current_time_millis;
			fd_flashing = true;
		}
		// Update AP FD engagement mode
		if ((!fd_on) && (!ap_on) ) fd_engagement = FDEngagement.NONE; 
				else if ((fd_on) && (!ap_on) ) fd_engagement = FDEngagement.FD ;
				else if ((!fd_on) && (ap_on) ) fd_engagement = FDEngagement.AP ;
				else fd_engagement = FDEngagement.FD_AP;
		// Detect AP reversion only for QPAC or JARDesign
		if (this.avionics.is_qpac()) {
			// v_mode == 107 stands for V/S or FPA
			int v_mode=this.avionics.qpac_ap_vertical_mode();		
			if (v_mode==107 && fd_reversion == FDReversion.NORMAL) {
				// This is a mode reversion to V/S
				fd_flashing_start = pfd_gc.current_time_millis;
				fd_flashing = true;
				fd_reversion = FDReversion.VS;				
			}
			if (v_mode != 107) fd_reversion =  FDReversion.NORMAL;
		} else if (this.avionics.is_jar_a320neo()) {
			// v_mode == 13 and 14 stands for V/S or FPA
			int v_mode=this.avionics.jar_a320neo_ap_vertical_mode();
			if ((v_mode == 13 || v_mode == 14) && fd_reversion == FDReversion.NORMAL) {
				// This is a mode reversion to V/S
				fd_flashing_start = pfd_gc.current_time_millis;
				fd_flashing = true;
				fd_reversion = FDReversion.VS;				
			}
			if (v_mode != 13 && v_mode != 14) fd_reversion =  FDReversion.NORMAL;
		}
		// Flashing command
		boolean fd_display_bars = true;
		if (fd_flashing) {
			if (pfd_gc.current_time_millis > fd_flashing_start + 10000) fd_flashing = false;
			if (fd_flashing && (pfd_gc.current_time_millis % 1000 < 500)) fd_display_bars = false; 
		}
		

		// Flight Director Display (VS/HDG mode)
		if ( fd_on && fd_display_bars ) {
			int fd_y;
			int fd_x = cx + (int)(down * (-bank+this.avionics.fd_roll()) / scale) / 3; // divide by 3 to limit deflection
			int fd_bar = left * 12 / 24;
			int fd_yaw = -10000;
			
			if ( this.avionics.is_x737() ) {
				fd_y = cy + (int)(down * (-this.avionics.fd_pitch()) / scale);
			} else if ( this.avionics.is_qpac() ) {
				fd_y = cy - (int)(fd_bar * (this.avionics.qpac_fd_hor_bar() - 1.0f ) );
				fd_x = cx + (int)(fd_bar * (this.avionics.qpac_fd_ver_bar() - 1.0f ) );
				fd_yaw = cx + (int)(fd_bar * (this.avionics.qpac_fd_yaw_bar() * 2.0f) );
			} else {
				fd_y = cy + (int)(down * (pitch-this.avionics.fd_pitch()) / scale);
			}
	
			// FD bars
			g2.setColor(pfd_gc.pfd_active_color);
			original_stroke = g2.getStroke();
			g2.setStroke(pfd_gc.adi_fd_bar_stroke);
			// horizontal
			if (fd_y < (cy+left*9/10)) g2.drawLine(cx - fd_bar, fd_y, cx + fd_bar, fd_y);
			// vertical or yaw bar
			if ((! airborne) || (ra < 30)) {
				// conditions to display yaw bar instead of vertical FD bar is below 30 ft radar or on ground (FCOM 1.31.40p2 n3)
				if (fd_yaw > (cx-left*9/10)) {
					int fd_thick = Math.round(3 * pfd_gc.grow_scaling_factor);
					int fd_yaw_x [] = {
							fd_yaw,
							fd_yaw-fd_thick,
							fd_yaw-fd_thick,
							fd_yaw+fd_thick,
							fd_yaw+fd_thick
					};
					int fd_yaw_y[] = {
							cy + 2,
							cy + fd_thick *3,
							cy + down * 9/24,
							cy + down * 9/24,
							cy + fd_thick *3
					};
					g2.drawPolygon(fd_yaw_x, fd_yaw_y, 5);
				}
			} else {
				if (fd_x > (cx-left*9/10)) g2.drawLine(fd_x, cy - fd_bar, fd_x, cy + fd_bar);
			}
			
			if (fd_x > (cx-left*9/10)) g2.drawLine(fd_x, cy - fd_bar, fd_x, cy + fd_bar);

			g2.setStroke(original_stroke);
		}

		if (path_director_on) {
			int fpd_bar = left * 12 / 24;
			
			int fpd_r = down/25;
			int fpd_r2 = down/45;	
			int fpd_w = down/4;
			int fpd_y = cy - (int)(fpd_bar * (this.avionics.qpac_fd_hor_bar() - 1.0f ) );
			int fpd_x = cx + (int)(fpd_bar * (this.avionics.qpac_fd_ver_bar() - 1.0f ) );
			float fpd_y_rad = (45.0f * (this.avionics.qpac_fd_ver_bar() - 1.0f ));
			g2.setColor(pfd_gc.pfd_active_color);
			g2.rotate(Math.toRadians(fpd_y_rad), fpd_x, fpd_y);
			g2.drawOval(fpd_x - fpd_r, fpd_y - fpd_r, fpd_r*2, fpd_r*2);
			g2.drawLine(fpd_x - fpd_r, fpd_y, fpd_x - fpd_w, fpd_y);
			g2.drawLine(fpd_x - fpd_w, fpd_y, fpd_x - fpd_w - fpd_r, fpd_y + fpd_r2);
			g2.drawLine(fpd_x - fpd_w, fpd_y, fpd_x - fpd_w - fpd_r, fpd_y - fpd_r2);
			g2.drawLine(fpd_x - fpd_w - fpd_r, fpd_y + fpd_r2, fpd_x - fpd_w - fpd_r, fpd_y - fpd_r2);
			
			g2.drawLine(fpd_x + fpd_r, fpd_y, fpd_x + fpd_w, fpd_y);
			g2.drawLine(fpd_x + fpd_w, fpd_y, fpd_x + fpd_w + fpd_r, fpd_y + fpd_r2);
			g2.drawLine(fpd_x + fpd_w, fpd_y, fpd_x + fpd_w + fpd_r, fpd_y - fpd_r2);
			g2.drawLine(fpd_x + fpd_w + fpd_r, fpd_y + fpd_r2, fpd_x + fpd_w + fpd_r, fpd_y - fpd_r2);
			g2.setTransform(original_at);
		}


		// bank marks
		// on AirBus, marks are outside
		g2.setClip(original_clipshape);
        drawBankMarks(g2, protections);

		g2.setTransform(original_at);
	}



	private void drawPitchmark(Graphics2D g2, float pitch, int pitchmark, int p_y, int p_90, int cx, int cy, int size, boolean protections) {

		int p_m = Math.round(pitch / 2.5f) * 25 + pitchmark;
		int m_y = p_y - p_90 * p_m / 900;
		int p_w = size;
		int prot_m_w = size / 6 ;
		Stroke original_stroke;
		if ( p_m % 100 == 0 ) {
			p_w = size * 7 / 16;
		} else if ( p_m % 50 == 0 ) {
			p_w = size / 4;
		} else if ( p_m % 25 == 0 ) {
			p_w = size / 12;
		}

		// FCOM 1.31.40 p4 (4) Pitch Scale (White)
		// Markers every 10° between -80° and +80°
		// every 2.5° between -10° and +30°
		
		if ( (( p_m <= 300 ) && ( p_m != 0 ) && ( p_m >= -200 )) 
				|| (p_m > 300 && p_m <= 800 && (p_m % 100) == 0) 
				|| ( p_m < -200 && p_m > -800 && (p_m % 100) == 0)) {
			g2.drawLine(cx - p_w, m_y, cx + p_w, m_y);
			if ( ( p_m != 0 ) && ( p_m % 100 == 0 ) ) {
				g2.setFont(pfd_gc.font_m);
				int f_w = pfd_gc.get_text_width(g2, pfd_gc.font_m, "00");
				int f_y = pfd_gc.line_height_m / 2 - 1;
				String pitch_str = "" + Math.abs(p_m/10);
				g2.drawString(pitch_str, cx - p_w - f_w - size/8, m_y + f_y);
				g2.drawString(pitch_str, cx + p_w + size/8, m_y + f_y);
			}
		}
		// protection marks
		if (( p_m/10 == -15) || (p_m/10 == 30)) {
			if ( ! protections) {
				// amber cross
				g2.setColor(pfd_gc.pfd_caution_color);
				g2.drawLine(cx - p_w - prot_m_w, m_y + p_w/4, cx - p_w, m_y - p_w/4);
				g2.drawLine(cx - p_w - prot_m_w, m_y - p_w/4, cx - p_w, m_y + p_w/4);
				g2.drawLine(cx + p_w + prot_m_w, m_y + p_w/4, cx + p_w, m_y - p_w/4);
				g2.drawLine(cx + p_w + prot_m_w, m_y - p_w/4, cx + p_w, m_y + p_w/4);
			} else {
				// green lines
				g2.setColor(pfd_gc.pfd_active_color);
				g2.drawLine(cx - p_w - prot_m_w, m_y+2, cx - p_w, m_y+2);
				g2.drawLine(cx - p_w - prot_m_w, m_y-2, cx - p_w, m_y-2);
				g2.drawLine(cx + p_w + prot_m_w, m_y+2, cx + p_w, m_y+2);
				g2.drawLine(cx + p_w + prot_m_w, m_y-2, cx + p_w, m_y-2);	
			}
			g2.setColor(pfd_gc.pfd_markings_color);
		}
		if (p_m == 350 || p_m == -250 || p_m == 500 || p_m == -400 ) {
			// Don't draw pitch mark, but arrow
			
			int pa_d = p_m > 0 ? 1 : -1;
			// BIG ARROW (pitch > 80°)
			// TODO : draw Big Arrow in pitch upset more than 80°
			/*
			int pa_x[] = { 
					cx - p_w,
					cx,
					cx+p_w,
					cx+p_w/4,
					cx+p_w/4,
					cx+p_w/3,
					cx,
					cx-p_w/3,
					cx-p_w/4,
					cx-p_w/4
			};
		
			int pa_y[] = {
					m_y - pa_d * p_w / 2,
					m_y + pa_d * p_w ,
					m_y - pa_d * p_w / 2,
					m_y - pa_d * p_w / 2,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 3,
					m_y,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 2			
			};
			*/
			// NORMAL ARROW
			int sa_x[] = {
					cx,
					cx - p_w/2,
					cx - p_w,
					cx,
					cx + p_w,
					cx + p_w/2
			};
			
			int sa_y[] = {
					m_y,
					m_y - pa_d * p_w,
					m_y - pa_d * p_w,
					m_y + pa_d * p_w,
					m_y - pa_d * p_w,
					m_y - pa_d * p_w					
			};
			g2.setColor(pfd_gc.warning_color);
			original_stroke = g2.getStroke();
			g2.setStroke(new BasicStroke(3.0f * pfd_gc.scaling_factor));
			g2.drawPolygon(sa_x, sa_y, 6);	
			g2.setColor(pfd_gc.pfd_markings_color);
			g2.setStroke(original_stroke);

		}
	}

	/**
	 * This method is time expensive - takes 1ms per cycle
	 * @param g2
	 * @param protections
	 */
	private void drawBankMarks(Graphics2D g2, boolean protections) {
		
		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int right = pfd_gc.adi_size_right;
		int up = pfd_gc.adi_size_up;
		int down = pfd_gc.adi_size_down;
		// on AirBus, marks are outside
	
		int level_triangle_x[] = { cx, cx - left/14, cx + right/14 };
		int level_triangle_y[] = { cy - up, cy - up*35/32, cy - up*35/32 };
		int bank_mark_thick = up/25;
		int bank_mark_heigth = up/20;
		int bank_mark_2heigth = up/13;
		int bank_mark_x = cx - bank_mark_thick / 2;
		int bank_mark_y = cy - up - bank_mark_heigth;
		int bank_mark_2y = cy - up - bank_mark_2heigth;
		
		g2.setColor(pfd_gc.pfd_reference_color);
		g2.drawPolygon(level_triangle_x, level_triangle_y, 3);
		
		g2.setColor(pfd_gc.pfd_markings_color);
		Stroke original_stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(1.5f * pfd_gc.scaling_factor));
		g2.drawArc(  cx - left,  cy - up, left + right, up + down, 59, 62);
		
		g2.rotate(Math.toRadians(+10), cx, cy);	
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(-10-10), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(+10+20), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(-20-20), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(+20+45), cx, cy);
		g2.drawLine(cx, cy - up, cx, cy - up - bank_mark_2heigth);
		g2.rotate(Math.toRadians(-45-45), cx, cy);
		g2.drawLine(cx, cy - up, cx, cy - up - bank_mark_2heigth);
		g2.rotate(Math.toRadians(+45+30), cx, cy);		
		g2.drawRect(bank_mark_x, bank_mark_2y, bank_mark_thick, bank_mark_2heigth);
		g2.rotate(Math.toRadians(-30-30), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_2y, bank_mark_thick, bank_mark_2heigth);

		
		// Airbus max bank protection mark is at 67 deg. (normal law)
		// double strikes become amber crosses with alternate & direct laws
		if (protections) {
			g2.setColor(pfd_gc.pfd_active_color);
			g2.rotate(Math.toRadians(+30+67), cx, cy);
			g2.drawLine(cx-2, cy - up - up/40, cx-2, cy - up + up/20);
			g2.drawLine(cx+2, cy - up - up/50, cx+2, cy - up + up/17);
			g2.rotate(Math.toRadians(-67-67), cx, cy);
			g2.drawLine(cx-2, cy - up - up/50, cx-2, cy - up + up/17);
			g2.drawLine(cx+2, cy - up - up/40, cx+2, cy - up + up/20);		
		} else {
			g2.setColor(pfd_gc.pfd_caution_color);
			g2.rotate(Math.toRadians(+30+67), cx, cy);
			g2.drawLine(cx-up/30, cy - up - up/30, cx+up/30, cy - up + up/30);
			g2.drawLine(cx+up/30, cy - up - up/30, cx-up/30, cy - up + up/30);
			g2.rotate(Math.toRadians(-67-67), cx, cy);
			g2.drawLine(cx-up/30, cy - up - up/30, cx+up/30, cy - up + up/30);
			g2.drawLine(cx+up/30, cy - up - up/30, cx-up/30, cy - up + up/30);		
		}
		g2.setStroke(original_stroke);
	}
	
	private void drawAircraftSymbol(Graphics2D g2, boolean acf_symbol_dimmed) {
		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int down = pfd_gc.adi_size_down;
		
		// aircraft symbol
		// boolean aircraft_dimmed = fpv_on && !fd_on;
		int wing_t = Math.round(3 * pfd_gc.grow_scaling_factor);
		int wing_i = left * 13 / 24;
		int wing_o = left * 21 / 24;
		int wing_h = down * 3 / 24;
		int left_wing_x[] = {
				cx - wing_i + wing_t,
				cx - wing_i + wing_t,
				cx - wing_i - wing_t,
				cx - wing_i - wing_t,
				cx - wing_o,
				cx - wing_o
		};
		int right_wing_x[] = {
				cx + wing_i - wing_t,
				cx + wing_i - wing_t,
				cx + wing_i + wing_t,
				cx + wing_i + wing_t,
				cx + wing_o,
				cx + wing_o
		};
		int wing_y[] = {
				cy - wing_t,
				cy + wing_h,
				cy + wing_h,
				cy + wing_t,
				cy + wing_t,
				cy - wing_t
		};
		g2.setColor(pfd_gc.background_color);
		g2.fillPolygon(left_wing_x, wing_y, 6);
		g2.fillPolygon(right_wing_x, wing_y, 6);
		// small square in the center (that's the rule on Airbus A320)
		g2.fillRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
		if (acf_symbol_dimmed) {
			g2.setColor(pfd_gc.pfd_reference_color.darker());
		} else {
			g2.setColor(pfd_gc.pfd_reference_color);
		}		
		g2.drawPolygon(left_wing_x, wing_y, 6);
		g2.drawPolygon(right_wing_x, wing_y, 6);
		g2.drawRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
		
	}
	
    /**
     * 
     * @param g2 : Graphic context
     * @param ra : int radio altitude in feet
     * @param cx : int ADI center X position in pixels
     */
	private void drawRadarAltitude(Graphics2D g2, int ra, int cx) {
		// Display Radar Altitude
		if  ( ra < 2500 )  {
			int caution_ra = 400; // default value when no DH set
			int d_ra_txt; // delta font width to align with the middle

			// above 50ft, round to 10
			// bellow 50 ft, round to 5
			// bellow 10 fr, don't round
			if ( ra > 50 ) {
				ra = ( ra + 5 ) / 10 * 10;

			} else if ( ra > 10 ) {
				ra = ( ra + 2 ) / 5 * 5;
			}

			// amber and bigger when ra < dh + 100 otherwise green
			String ra_str = "" + ra;
			if ( ra < caution_ra ) {
				g2.setColor(pfd_gc.pfd_caution_color);
				g2.setFont(pfd_gc.font_xxl);
				d_ra_txt = pfd_gc.get_text_width(g2, pfd_gc.font_xxl, ra_str) / 2;
			} else {
				g2.setColor(pfd_gc.pfd_active_color);
				g2.setFont(pfd_gc.font_xl);
				d_ra_txt = pfd_gc.get_text_width(g2, pfd_gc.font_xl, ra_str) / 2 ;
			}

			// digital readout of the current RA
			g2.drawString(ra_str, cx - d_ra_txt, pfd_gc.adi_cy + pfd_gc.adi_size_down - pfd_gc.line_height_xl/2);
		}
	}

	// This function should be in the ILS_A320 class
	private void drawMarker(Graphics2D g2) {

		if ( this.avionics.outer_marker() || this.avionics.middle_marker() || this.avionics.inner_marker() ) {

			//int m_r = pfd_gc.adi_size_right*2/16;
			int m_x = pfd_gc.adi_cx + pfd_gc.adi_size_right;
			int m_y = pfd_gc.adi_cy + pfd_gc.adi_size_down; // - pfd_gc.line_height_xl/2

			//g2.setColor(pfd_gc.background_color);
			//g2.fillOval(m_x, m_y, 2*m_r, 2*m_r);

			String mstr = "";
			if ( this.avionics.outer_marker() ) {
				g2.setColor(pfd_gc.pfd_selected_color);
				mstr = "OM";
			} else if ( this.avionics.middle_marker() ) {
				g2.setColor(pfd_gc.pfd_caution_color);
				mstr = "MM";
			} else {
				g2.setColor(pfd_gc.pfd_markings_color);
				mstr = "IM";
			}

			g2.setFont(pfd_gc.font_xl);
			g2.drawString(mstr, m_x - pfd_gc.get_text_width(g2, pfd_gc.font_xl, mstr), m_y  );

		}

	}

    private void drawStrickPriorityMessage(Graphics2D g2){
    	StickPriorityMessage priority_message = aircraft.stick_priority_messages();
    	// aircraft.xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT );
    	switch (priority_message) {
    		case NONE : 
    			stick_priority_box.clearText();
    			break;
    		case DUAL_INPUT :
    			stick_priority_box.setText("DUAL INPUT", FE_Color.ALARM);    	
    			stick_priority_box.paint(g2);
    			break;
    		case PRIORITY_LEFT :
    			stick_priority_box.setText("PRIORITY LEFT", FE_Color.ALARM);    	
    			stick_priority_box.paint(g2);
    			break;
    		case PRIORITY_RIGHT :
    			stick_priority_box.setText("PRIORITY RIGHT", FE_Color.ALARM);    	
    			stick_priority_box.paint(g2);
    			break;
    	}
    }

    /**
     * 
     * @param g2 : Graphic Context
     * @param ra : radio altitude in feet
     * @param airborne : boolean true if aircraft is airborne
     * @param engine_started : boolean true if one engine is started
     */
    private void drawStickOrders(Graphics2D g2, int ra, boolean airborne, boolean engine_started) {

    	DrawYokeInputMode display_yoke_pref = preferences.get_pfd_draw_yoke_input();
    	// Stick orders : on ground / bellow 30 ft AGL
    	boolean display_stick_always = (display_yoke_pref == DrawYokeInputMode.ALWAYS) || (display_yoke_pref == DrawYokeInputMode.ALWAYS_RUDDER);
    	boolean display_stick_orders = ((! airborne) || (ra < 30)) && engine_started && (display_yoke_pref != DrawYokeInputMode.NONE);
    	// public enum DrawYokeInputMode { NONE, AUTO, AUTO_RUDDER, ALWAYS, ALWAYS_RUDDER };
    	boolean display_rudder = (display_yoke_pref == DrawYokeInputMode.AUTO_RUDDER ) || (display_yoke_pref == DrawYokeInputMode.ALWAYS_RUDDER );
    	if  (display_stick_orders || display_stick_always)  {

    		g2.setColor(pfd_gc.pfd_markings_color);
    		int st_width = pfd_gc.adi_size_left / 8;
    		int st_left = pfd_gc.adi_cx - pfd_gc.adi_size_left*14/20;
    		int st_right = pfd_gc.adi_cx + pfd_gc.adi_size_right*14/20;
    		int st_up = pfd_gc.adi_cy - pfd_gc.adi_size_up/2;
    		int st_down = pfd_gc.adi_cy + pfd_gc.adi_size_down/2;
    		int st_x = pfd_gc.adi_cx + Math.round(this.aircraft.yoke_roll() * pfd_gc.adi_size_left*14/20);
    		int st_y = pfd_gc.adi_cy - Math.round(this.aircraft.yoke_pitch() * pfd_gc.adi_size_up/2);
    		int st_d = pfd_gc.adi_size_left/60;
    		int st_w = pfd_gc.adi_size_left/10;
    		int rd_x = pfd_gc.adi_cx + Math.round(this.aircraft.rudder_hdg() * pfd_gc.adi_size_left*14/20);
    		int rd_y = pfd_gc.adi_cy + pfd_gc.adi_size_up/2 - st_d/2;
    		int brk_l_y = Math.round(this.aircraft.brk_pedal_left() * pfd_gc.adi_size_up/2);
    		int brk_r_y = Math.round(this.aircraft.brk_pedal_right() * pfd_gc.adi_size_up/2);

    		// Stick box
    		// top left
    		g2.drawLine(st_left, st_up, st_left + st_width, st_up);
    		g2.drawLine(st_left, st_up, st_left, st_up + st_width);
    		// top right
    		g2.drawLine(st_right, st_up, st_right - st_width, st_up);
    		g2.drawLine(st_right, st_up, st_right, st_up + st_width);
    		// bottom left
    		g2.drawLine(st_left, st_down, st_left + st_width, st_down);
    		g2.drawLine(st_left, st_down, st_left, st_down - st_width);
    		// bottom right
    		g2.drawLine(st_right, st_down, st_right - st_width, st_down);
    		g2.drawLine(st_right, st_down, st_right, st_down - st_width);

    		// Stick marker
    		// top left
    		g2.drawLine(st_x - st_d - st_w , st_y - st_d, st_x - st_d, st_y - st_d);
    		g2.drawLine(st_x - st_d, st_y - st_d, st_x - st_d, st_y - st_d - st_w);
    		// top right
    		g2.drawLine(st_x + st_d + st_w , st_y - st_d, st_x + st_d, st_y - st_d);
    		g2.drawLine(st_x + st_d, st_y - st_d, st_x + st_d, st_y - st_d - st_w);
    		// bottom left
    		g2.drawLine(st_x - st_d - st_w , st_y + st_d, st_x - st_d, st_y + st_d);
    		g2.drawLine(st_x - st_d, st_y + st_d, st_x - st_d, st_y + st_d + st_w);
    		// bottom right
    		g2.drawLine(st_x + st_d + st_w , st_y + st_d, st_x + st_d, st_y + st_d);
    		g2.drawLine(st_x + st_d, st_y + st_d, st_x + st_d, st_y + st_d + st_w);

    		if (display_rudder) {
    			// Rudder marker
    			g2.drawRect(rd_x-st_d*2, rd_y, st_d*4, st_d*2);
    			g2.drawLine(pfd_gc.adi_cx, st_down, pfd_gc.adi_cx, st_down+st_d);

    			// Brake pedals
    			g2.fillRect(st_left-st_d*2, pfd_gc.adi_cy + pfd_gc.adi_size_up/2 - brk_l_y, st_d*2, brk_l_y);
    			g2.fillRect(st_right      , pfd_gc.adi_cy + pfd_gc.adi_size_up/2 - brk_r_y, st_d*2, brk_r_y);
    		}

    	}
    }
}
