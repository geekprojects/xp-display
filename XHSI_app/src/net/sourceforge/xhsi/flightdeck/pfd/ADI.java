/**
* ADI.java
* 
* Renders an Attitude & Director Indicator
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
// import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.XHSIPreferences.DrawYokeInputMode;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.util.FramedElement.FE_Color;


public class ADI extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    PFDFramedElement failed_att_flag;
    PFDFramedElement failed_fd_flag;
    PFDFramedElement failed_fpv_flag;
    PFDFramedElement failed_ra_flag;
    PFDFramedElement stick_priority_box;

    public ADI(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        failed_att_flag = new PFDFramedElement(PFDFramedElement.ATT_FLAG, 0, hsi_gc, FE_Color.CAUTION);
        failed_att_flag.setFrameOptions(true, false, false, FE_Color.CAUTION);
        failed_att_flag.disableFlashing();
        
        failed_fd_flag = new PFDFramedElement(PFDFramedElement.FD_FLAG, 0, hsi_gc, FE_Color.CAUTION);
        failed_fd_flag.setFrameOptions(true, false, false, FE_Color.CAUTION);
        failed_fd_flag.disableFlashing();
        
        failed_fpv_flag = new PFDFramedElement(PFDFramedElement.FPV_FLAG, 0, hsi_gc, FE_Color.CAUTION);
        failed_fpv_flag.setFrameOptions(true, false, false, FE_Color.CAUTION);
        failed_fpv_flag.disableFlashing();
        
        failed_ra_flag = new PFDFramedElement(PFDFramedElement.RA_FLAG, 0, hsi_gc, FE_Color.CAUTION);
        failed_ra_flag.setFrameOptions(true, false, false, FE_Color.CAUTION);
        failed_ra_flag.disableFlashing();
        
        stick_priority_box = new PFDFramedElement(PFDFramedElement.ATT_FLAG, 0, hsi_gc, FE_Color.ALARM);
        stick_priority_box.enableFlashing();
        stick_priority_box.disableFraming();
        stick_priority_box.setBigFont(true);
    }


    public void paint(Graphics2D g2) {
		if ( pfd_gc.boeing_style ) {
			if ( ! XHSIStatus.receiving  || ! this.avionics.att_valid() ) {
				// 737 FCOM 10.11.31 (15) ATT 
				// if the PFD loses attitude data, its entire sphere is cleared to display the ATT flag (red)
				if ( pfd_gc.powered ) {
					drawAirplaneSymbol(g2);
					drawFailedADI(g2);
				}
			} else if ( pfd_gc.powered ) {
				failed_att_flag.clearText();
				failed_fd_flag.clearText();
				failed_fpv_flag.clearText();
				failed_ra_flag.clearText();
				drawADI(g2);
				drawMarker(g2);
	            if ( this.preferences.get_draw_pfd_turnrate() ) {
	                drawTurnRate(g2);
	                if ( ! this.aircraft.on_ground() ) drawBankForStdRate(g2);
	            }
				// drawStrickPriorityMessage(g2);
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
		drawBankMarks(g2);
    	failed_att_flag.setText("ATT", FE_Color.CAUTION);
    	failed_att_flag.paint(g2);
    	failed_fpv_flag.setText("FPV", FE_Color.CAUTION);
    	failed_fpv_flag.paint(g2);
    	failed_fd_flag.setText("FD", FE_Color.CAUTION);
    	failed_fd_flag.paint(g2);
    	// failed_ra_flag.setText("RA", PFE_Color.PFE_COLOR_CAUTION);
    	// failed_ra_flag.paint(g2);
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

        boolean colorgradient_horizon = this.preferences.get_draw_colorgradient_horizon();

        float pitch = this.aircraft.pitch(); // degrees
        float bank = this.aircraft.bank(); // degrees

        // full-scale pitch down = adi_pitchscale (eg: 22°)
        int pitch_y = cy + (int)(down * pitch / scale);

        Shape original_clipshape = g2.getClip();
        if ( ! colorgradient_horizon ) {
            g2.clipRect(cx - left, cy - up, left + right, up + down);
        } else if ( this.preferences.get_draw_fullwidth_horizon() ) {
            if ( pfd_gc.draw_hsi ) {
                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.panel_rect.y, pfd_gc.panel_rect.width, pfd_gc.dg_cy - pfd_gc.dg_radius - pfd_gc.hsi_tick_w - pfd_gc.line_height_xl*3/2 - pfd_gc.panel_rect.y);
            } else {
//                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.tape_top - 1, pfd_gc.panel_rect.width, pfd_gc.tape_height + 2);
                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.panel_rect.y + pfd_gc.panel_offset_y, pfd_gc.panel_rect.width, pfd_gc.panel_rect.width);
            }
        }
        
        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(-bank), cx, cy);

        int diagonal = colorgradient_horizon ?
            (int)Math.hypot( Math.max(cx, pfd_gc.panel_rect.width - cx), Math.max(cy, pfd_gc.panel_rect.height - cy) ) :
            (int)Math.hypot( Math.max(left, right), Math.max(up, down) );

        if ( colorgradient_horizon ) {

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

        } else {
            g2.setColor(pfd_gc.sky_color);
            g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90);
            g2.setColor(pfd_gc.ground_color);
            g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90);
        }

        g2.setColor(pfd_gc.markings_color);
        g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);

        g2.setTransform(original_at);

        if ( this.preferences.get_draw_roundedsquare_horizon() ) {
            g2.setColor(pfd_gc.background_color);
            g2.fill(pfd_gc.adi_area);
        }


        // pitch marks
        g2.clipRect(
                cx - left + left/16,
                cy - up + up/16 + up/8 + up/24,
                left - left/16 + right - right/16,
                up - up/16 - up/8 - up/24 + down - down/16
            );

        g2.rotate(Math.toRadians(-bank), cx, cy);

        g2.setColor(pfd_gc.markings_color);
        drawPitchmark(g2, pitch, +175, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +150, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +125, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +100, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +75, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +50, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +25, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,    0, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -25, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -50, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -75, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -100, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -125, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -150, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -175, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -200, pitch_y, p_90, cx, cy, left);

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
            cx - left/12,
            cx + right/12 };
        int[] bank_pointer_y = {
            cy - up + up/16,
            cy - up + up/16 + up/8,
            cy - up + up/16 + up/8 };
        if ( Math.abs(bank) > 35.0f ) {
            if ( Math.abs(bank) > 70.0f ) {
                g2.setColor(pfd_gc.warning_color);
            } else {
                g2.setColor(pfd_gc.caution_color);
            }
            g2.fillPolygon(bank_pointer_x, bank_pointer_y, 3);
        }
        g2.drawPolygon(bank_pointer_x, bank_pointer_y, 3);

        // slip/skid indicator
        float ss = - this.aircraft.sideslip();
        int ss_x;
        if ( Math.abs(ss) > 10.0f ) {
            ss = 10.0f * Math.signum(ss);
            ss_x = cx - Math.round(ss * left/6 / 10.0f);
            g2.fillRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);
        } else {
            ss_x = cx - Math.round(ss * left/6 / 10.0f);
        }
        g2.drawRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);

        g2.setTransform(original_at);

        Stroke original_stroke = g2.getStroke();

        
        // FPV
        if ( ! this.aircraft.on_ground() ) {

            int dx = (int)(down * this.aircraft.drift() / scale);
            int dy = (int)(down * this.aircraft.aoa() / scale);
            if ( (Math.abs(dx) < down) && (Math.abs(dy) < down) ) {

                int fpv_x = cx - dx;
                int fpv_y = cy + dy;
                int fpv_r = down/20;
                g2.setColor(pfd_gc.fpv_color);
                g2.setStroke(pfd_gc.adi_fpv_stroke);
                g2.drawOval(fpv_x - fpv_r, fpv_y - fpv_r, fpv_r*2, fpv_r*2);
                g2.drawLine(fpv_x, fpv_y - fpv_r, fpv_x, fpv_y - fpv_r*25/10);
                g2.drawLine(fpv_x - fpv_r, fpv_y, fpv_x - fpv_r*4, fpv_y);
                g2.drawLine(fpv_x + fpv_r, fpv_y, fpv_x + fpv_r*4, fpv_y);
                g2.setStroke(original_stroke);

            }

        }
      

        // airplane symbol
        if ( ! this.preferences.get_single_cue_fd() ) {
        	drawAirplaneSymbol(g2);
        }

        // TODO Draw Pitch Limit 10.11.14 (2) Ambert
        // Indicates pitch limit
        // Displayed when flaps are not up
        // Displayed at low speeds with flaps up

        // FD
        if ( this.avionics.autopilot_mode() >= 1 ) {

            float acf_pitch = this.avionics.acf_pitch(); // degrees
            float acf_bank = this.avionics.acf_bank(); // degrees

            int fd_x;
            int fd_y;
            if ( this.avionics.is_x737() ) {
                fd_x = cx + (int)(down * (-acf_bank+this.avionics.fd_roll()) / scale) / 2; // divide by 2 to limit deflection
                fd_y = cy + (int)(down * (-this.avionics.fd_pitch()) / scale);
            } else {
                fd_x = cx + (int)(down * (-acf_bank+this.avionics.fd_roll()) / scale) / 3; // divide by 3 to limit deflection
                fd_y = cy + (int)(down * (acf_pitch-this.avionics.fd_pitch()) / scale);
            }

            g2.setColor(pfd_gc.heading_bug_color);

            if ( this.preferences.get_single_cue_fd() ) {
                
                // V-bar
                g2.rotate(Math.toRadians(-acf_bank+this.avionics.fd_roll()), cx, fd_y);

                int bar_o = left * 9 / 16;
                int bar_d = down / 5;
                int bar_h = down / 28;
                int bar_w = left / 10;
                int left_bar_x[] = {
                    cx - 2,
                    cx - bar_o - 2,
                    cx - bar_o - bar_w - 1
                };
                int right_bar_x[] = {
                    cx + 2,
                    cx + bar_o + 2,
                    cx + bar_o + bar_w + 1
                };
                int bar_y[] = {
                    fd_y,
                    fd_y + bar_d,
                    fd_y + bar_d - bar_h
                };
                g2.drawPolygon(left_bar_x, bar_y, 3);
                g2.drawPolygon(right_bar_x, bar_y, 3);
                g2.fillPolygon(left_bar_x, bar_y, 3);
                g2.fillPolygon(right_bar_x, bar_y, 3);
                int left_tri_x[] = {
                    cx - bar_o - bar_w - 3,
                    cx - bar_o - 2,
                    cx - bar_o - bar_w - 3
                };
                int right_tri_x[] = {
                    cx + bar_o + bar_w + 3,
                    cx + bar_o + 2,
                    cx + bar_o + bar_w + 3
                };
                int tri_y[] = {
                    fd_y + bar_d + bar_h,
                    fd_y + bar_d,
                    fd_y + bar_d - bar_h
                };
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillPolygon(left_tri_x, tri_y, 3);
                g2.fillPolygon(right_tri_x, tri_y, 3);
                g2.setColor(pfd_gc.heading_bug_color);
                g2.drawPolygon(left_tri_x, tri_y, 3);
                g2.drawPolygon(right_tri_x, tri_y, 3);

                g2.setTransform(original_at);

            } else {

                // cross-hair
                int fd_bar = down * 5 /8;
                original_stroke = g2.getStroke();
                g2.setStroke(pfd_gc.adi_fd_bar_stroke);
                // hor
                g2.drawLine(cx - fd_bar, fd_y, cx + fd_bar, fd_y);
                // vert
                g2.drawLine(fd_x, cy - fd_bar, fd_x, cy + fd_bar);
                g2.setStroke(original_stroke);

            }

        }


        if ( this.preferences.get_single_cue_fd() ) {

            // Delta airplane
            int delta_i = left / 4;
            int delta_o = left * 9 / 16;
            int delta_h = down / 5;
            int left_delta_x[] = {
                cx,
                cx - delta_i,
                cx - delta_o
            };
            int right_delta_x[] = {
                cx,
                cx + delta_i,
                cx + delta_o
            };
            int delta_y[] = {
                cy,
                cy + delta_h,
                cy + delta_h
            };
            g2.setColor(pfd_gc.background_color);
            g2.fillPolygon(left_delta_x, delta_y, 3);
            g2.fillPolygon(right_delta_x, delta_y, 3);
            g2.setColor(pfd_gc.markings_color);
            g2.drawPolygon(left_delta_x, delta_y, 3);
            g2.drawPolygon(right_delta_x, delta_y, 3);
            
            // Horizon Reference Bars
            g2.setColor(pfd_gc.background_color);
            g2.fillRect(cx - left * 15 / 16, cy - up / 36, left * 4 / 16, down / 18);
            g2.fillRect(cx + right * 11 / 16, cy - up / 36, left * 4 / 16, down / 18);
            g2.setColor(pfd_gc.markings_color);
            g2.drawRect(cx - left * 15 / 16, cy - up / 36, left * 4 / 16, down / 18);
            g2.drawRect(cx + right * 11 / 16, cy - up / 36, left * 4 / 16, down / 18);
            

        } else {
            // small square in the center
            g2.setColor(pfd_gc.markings_color);
            int wing_t = Math.round(4 * pfd_gc.grow_scaling_factor);
            g2.drawRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
        }


        // bank marks
        drawBankMarks(g2);

        g2.setTransform(original_at);

        g2.setClip(original_clipshape);

//        g2.setColor(pfd_gc.instrument_background_color);
//        g2.fillRect(pfd_gc.border_left + ( pfd_gc.frame_size.width - pfd_gc.border_left - pfd_gc.border_right ) / 32, pfd_gc.border_top + ( pfd_gc.frame_size.height - pfd_gc.border_top - pfd_gc.border_bottom ) / 8, ( pfd_gc.frame_size.width - pfd_gc.border_left - pfd_gc.border_right ) / 8, ( pfd_gc.frame_size.height - pfd_gc.border_top - pfd_gc.border_bottom ) / 8 * 6);
       
		/*
		 * Stick orders - control commands displayed on the PFD inside the ADI
		 * Normal mode: displayed only when the aircraft is on ground and engine started
		 */
        // TODO: Get real radio altitude indicator
		int ra = Math.round(this.aircraft.agl_m() * 3.28084f); // Radio altitude
		drawStickOrders(g2, ra, !this.aircraft.on_ground(), engineStarted());	
       
    }


//    private int markWidth(int p_m, int size) {
//        int p_w = size;
//        return p_w;
//    }


    private void drawPitchmark(Graphics2D g2, float pitch, int pitchmark, int p_y, int p_90, int cx, int cy, int size) {

        int p_m = Math.round(pitch / 2.5f) * 25 + pitchmark;
        int m_y = p_y - p_90 * p_m / 900;
        int p_w = size;
        if ( p_m % 100 == 0 ) {
            p_w = size * 7 / 16;
        } else if ( p_m % 50 == 0 ) {
            p_w = size / 4;
        } else if ( p_m % 25 == 0 ) {
            p_w = size / 12;
        }

        if ( ( p_m <= 900 ) && ( p_m != 0 ) && ( p_m >= -900 ) ) {
            g2.drawLine(cx - p_w, m_y, cx + p_w, m_y);
            if ( ( p_m != 0 ) && ( p_m % 100 == 0 ) ) {
                g2.setFont(pfd_gc.font_s);
                int f_w = pfd_gc.get_text_width(g2, pfd_gc.font_s, "00");
                int f_y = pfd_gc.line_height_s / 2 - 2;
                String pitch_str = "" + Math.abs(p_m/10);
                g2.drawString(pitch_str, cx - p_w - f_w - 3, m_y + f_y);
                g2.drawString(pitch_str, cx + p_w + 3, m_y + f_y);
            }
        }

    }

	/**
	 * This method is time expensive - takes 1ms per cycle
	 * @param g2
	 * @param protections
	 */
	private void drawBankMarks(Graphics2D g2) {
		AffineTransform original_at = g2.getTransform();
		
		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int right = pfd_gc.adi_size_right;
		int up = pfd_gc.adi_size_up;
		
        int level_triangle_x[] = { cx, cx - left/20 - left/40, cx + right/20 + right/40 };
        int level_triangle_y[] = { cy - up + up/16, cy - up - up/32, cy - up - up/32 };
        g2.setColor(pfd_gc.markings_color);
        g2.fillPolygon(level_triangle_x, level_triangle_y, 3);
        g2.rotate(Math.toRadians(+10), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-10-10), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+10+20), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-20-20), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+20+45), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-45-45), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+45+30), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-30-30), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+30+60), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-60-60), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.setTransform(original_at);
	}

    private void drawMarker(Graphics2D g2) {

        if ( this.avionics.outer_marker() || this.avionics.middle_marker() || this.avionics.inner_marker() ) {

            int m_r = pfd_gc.adi_size_right*2/16;
            int m_x;
            int m_y;
            if ( this.preferences.get_draw_fullwidth_horizon() ) {
                m_x = pfd_gc.adi_cx - pfd_gc.adi_size_left;
                m_y = pfd_gc.adi_cy - pfd_gc.adi_size_up;
            } else {
                m_x = pfd_gc.adi_cx + pfd_gc.adi_size_right - pfd_gc.adi_size_right*1/16 - 2*m_r;
                m_y = pfd_gc.adi_cy - pfd_gc.adi_size_up + pfd_gc.adi_size_right*1/16;
            }

            g2.setColor(pfd_gc.background_color);
            g2.fillOval(m_x, m_y, 2*m_r, 2*m_r);

            String mstr = "";
            if ( this.avionics.outer_marker() ) {
            	// TODO: use dimmable color !
                g2.setColor(Color.BLUE);
                mstr = "OM";
            } else if ( this.avionics.middle_marker() ) {
                g2.setColor(pfd_gc.caution_color);
                mstr = "MM";
            } else {
                g2.setColor(pfd_gc.markings_color);
                mstr = "IM";
            }

            Stroke original_stroke = g2.getStroke();
            g2.setStroke(pfd_gc.adi_ils_marker_stroke);
            g2.drawOval(m_x, m_y, 2*m_r, 2*m_r);
            g2.setStroke(original_stroke);

            g2.setFont(pfd_gc.font_m);
            g2.drawString(mstr, m_x + m_r - pfd_gc.get_text_width(g2, pfd_gc.font_m, mstr)/2, m_y + m_r + pfd_gc.line_height_m/2 - 2);

        }
        
    }
    
    
    private void drawTurnRate(Graphics2D g2) {
        
        int turnrate_x = pfd_gc.adi_cx;
        int turnrate_w = pfd_gc.adi_size_left*6/7;
        int turnrate_y = pfd_gc.adi_cy - pfd_gc.adi_size_up - pfd_gc.adi_size_up/11;
        int turnrate_h = pfd_gc.adi_size_up/12;

        float turnrate = this.aircraft.turn_rate() / 30.0f; // ratio of standard rate = 1.0f
        turnrate = Math.min(turnrate, 2.0f); // full scale right
        turnrate = Math.max(turnrate, -2.0f); // full scale left
        if ( Math.abs(turnrate) == 2.0f ) {
            g2.setColor(pfd_gc.caution_color);
        } else {
            g2.setColor(pfd_gc.markings_color);
        }
        int turnrate_d = Math.round(1000.0f * turnrate) * turnrate_w/2/2 / 1000;
        if ( turnrate_d > 0 ) {
            g2.fillRect(turnrate_x, turnrate_y - turnrate_h/2*3/4, turnrate_d, turnrate_h*6/8);
        } else {
            g2.fillRect(turnrate_x + turnrate_d, turnrate_y - turnrate_h/2*3/4, - turnrate_d, turnrate_h*6/8);
        }

        g2.setColor(pfd_gc.dim_markings_color);
        g2.drawRect(turnrate_x - turnrate_w/2, turnrate_y - turnrate_h/2, turnrate_w, turnrate_h);
        // vertical line at the center
        g2.drawLine(turnrate_x, turnrate_y - turnrate_h/2, turnrate_x, turnrate_y + turnrate_h/2);
        g2.setColor(pfd_gc.normal_color);
        // vertical line for standard rate left at 1/3 full scale
        g2.drawLine(turnrate_x - turnrate_w/2/2, turnrate_y - turnrate_h/2 + 1, turnrate_x - turnrate_w/2/2, turnrate_y + turnrate_h/2);
        // vertical line for standard rate right at 1/3 full scale
        g2.drawLine(turnrate_x + turnrate_w/2/2, turnrate_y - turnrate_h/2 + 1, turnrate_x + turnrate_w/2/2, turnrate_y + turnrate_h/2);

    }
    
    
    private void drawBankForStdRate(Graphics2D g2) {
    
        AffineTransform original_at = g2.getTransform();

        double targetbank = Math.atan( this.aircraft.true_air_speed() / 364.0 );
        
        if ( targetbank > 0.7854 ) {
            // > 45deg
            g2.setColor(pfd_gc.warning_color);
        } else if ( targetbank > 0.4363 ) {
            // > 25deg
            g2.setColor(pfd_gc.caution_color);
        } else {
            g2.setColor(pfd_gc.normal_color);
        }

        g2.rotate(targetbank, pfd_gc.adi_cx, pfd_gc.adi_cy);
        g2.drawOval(pfd_gc.adi_cx - pfd_gc.adi_size_left/50, pfd_gc.adi_cy - pfd_gc.adi_size_up + pfd_gc.adi_size_up/16 - pfd_gc.adi_size_up/25, pfd_gc.adi_size_left/25, pfd_gc.adi_size_up/25);
        g2.setTransform(original_at);

        g2.rotate(-targetbank, pfd_gc.adi_cx, pfd_gc.adi_cy);
        g2.drawOval(pfd_gc.adi_cx - pfd_gc.adi_size_left/50, pfd_gc.adi_cy - pfd_gc.adi_size_up + pfd_gc.adi_size_up/16 - pfd_gc.adi_size_up/25, pfd_gc.adi_size_left/25, pfd_gc.adi_size_up/25);
        g2.setTransform(original_at);

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
    
    private void drawAirplaneSymbol(Graphics2D g2) {
		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int down = pfd_gc.adi_size_down;
        int wing_t = Math.round(4 * pfd_gc.grow_scaling_factor);
        int wing_i = left / 3;
        int wing_o = left * 7 / 8;
        int wing_h = down / 8;
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
        g2.setColor(pfd_gc.markings_color);
        g2.drawPolygon(left_wing_x, wing_y, 6);
        g2.drawPolygon(right_wing_x, wing_y, 6);
        
        // small square in the center
        g2.setColor(pfd_gc.background_color);
        g2.fillRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
        g2.setColor(pfd_gc.markings_color);
        g2.drawRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
    }
    
    private boolean engineStarted() {
    	boolean engine_started = false;
    	float max_n1=0.0f;
    	float min_n1=1000.0f;
    	float n1;
    	for (int pos=0; pos<this.aircraft.num_engines(); pos++) {
    		n1 = this.aircraft.get_N1(pos);
    		if (n1 > max_n1) max_n1 = n1; 
    		if (n1 < min_n1) min_n1 = n1;
    		if (n1 > 5.0f) engine_started = true;
    	}
    	return engine_started;
    }
    
}
