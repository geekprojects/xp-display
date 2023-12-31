/**
 * PFDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2022  Nicolas Carel
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
// import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class PFDGraphicsConfig extends GraphicsConfig implements ComponentListener {

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Composite orig_cmpst;
    private boolean draw_transparent;
    
    public int instrument_size;
    public int panel_offset_y;
    
    // ADI
    public int adi_cx;
    public int adi_cy;
    public int adi_size_left;
    public int adi_size_right;
    public int adi_size_up;
    public int adi_size_down;
    public int adi_pitchscale;
    public int adi_pitch90;
    public int adi_att_flag_y;
    public int adi_ra_flag_x;
    public int adi_ra_flag_y;
    public int adi_fd_flag_y;
    public int adi_fd_flag_x;
    public int adi_fpv_flag_x;
    public Area adi_roundrectarea;
    public Area adi_area;
    public BasicStroke adi_fd_bar_stroke;
    public BasicStroke adi_fpv_stroke;
    public BasicStroke adi_ils_marker_stroke;
    
    public int tape_top;
    public int tape_height;
    public int tape_width;
    
    
    // FMA
    public int fma_left;
    public int fma_width;
    public int fma_top;
    public int fma_height;
    public int fma_col_1;
    public int fma_col_2;
    public int fma_col_3;
    public int fma_col_4;
    
    // AOA
    public int adi_aoa_flag_x;
    public int adi_aoa_flag_y;
    
    public int dg_radius;
    public int dg_cx;
    public int dg_cy;
    public boolean full_rose;
    public boolean draw_hsi;
    public int hsi_tick_w;
    
    // Vertical Speed Indicator
    public int vsi_left;
    public int vsi_width;
    public int vsi_top;
    public int vsi_height;
    public int vsi_flag_cx;
    public int vsi_flag_y;
    
    public int gs_width;
    public int gs_height;
    public int cdi_width;
    public int cdi_height;
    public int ra_x;
    public int ra_high_y;
    public int ra_low_y;
    public int ra_r;
    public int aoa_x;
    public int aoa_y;
    public int aoa_r;
    public int radios_top;
    public int radios_width;
    public int navradios_left;
    public int comradios_left;
    public int radios_height;
    
    public int comm_lost_y;
    
    // Only for Airbus
    
    // Speed tape
    public int speedtape_left;
    public int speed_flag_y;
    public int speed_flag_cx;
    
    // ADI
    public Area adi_airbus_horizon_area;
    public Area adi_square_horizon_area;
    public Area adi_pitchmark_area;
    public BasicStroke adi_hdg_bug_stroke;
    
    // Heading tape
    public int hdg_top;
    public int hdg_left;
    public int hdg_height;
    public int hdg_width;
	public int hdg_diamond_shift;
	public int hdg_diamond_size;
	public BufferedImage hdg_diamond_img;
	
    // ILS Data
    public int ils_line1;
    public int ils_line2;
    public int ils_line3;
    public int ils_x;
    
    // Altimeter tape
    public int altitape_left;
    public int altitape_right;
    public int[] alti_box_x;
    public int[] alti_box_y;
    public int[] alti_bug_x;
    public int alti_ind_rect_x;
    public int alti_ind_rect_y;
    public int alti_ind_rect_w;
    public int alti_ind_rect_h;
    public Area alti_ind_area;
    public Area alti_outside_area;
    public int alti_flag_cx;
    public int alti_flag_x;
    public int alti_flag_y;

    // g2.setStroke(new BasicStroke(3.5f * pfd_gc.scaling_factor));
    public BasicStroke speedtape_stroke_thick;
    // g2.setStroke(new BasicStroke(0.6f * pfd_gc.scaling_factor));
    public BasicStroke speedtape_stroke_thin;
    
    public PFDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }

    
    public void update_config(Graphics2D g2, boolean power, int instrument_style, float du_brightness) {

    	// Update colors if du_brightness changed
    	colors_updated = update_colors(du_brightness);
    
    	boolean settings_updated = (this.resized
                || this.reconfig
                || (this.powered != power)
                || (this.style != instrument_style)
            );
    	
        if (settings_updated) {
            // one of the settings has been changed

            // remember the new settings
            this.powered = power;
            this.style = instrument_style;
            
            // general instrument config
            super.update_config(g2);

            // specific instrument config
            instrument_size = Math.min(this.panel_rect.width, this.panel_rect.height);
            panel_offset_y = 0;
            if ( this.panel_rect.height * 100 / this.panel_rect.width > 180 ) {
                panel_offset_y = ( this.panel_rect.height - this.panel_rect.width*180/100 ) / 2;
            }
            // expanded rose scale when only the top of the DG is visible
            full_rose =  ( this.panel_rect.height > this.panel_rect.width * 105/100 );
            // HSI mode when the display is much higher than wide
            //draw_hsi =  ( this.panel_rect.height > this.panel_rect.width * 131/100 );
            // no longer automatic...
            draw_hsi = this.preferences.get_pfd_draw_hsi();
            //adi_cx = this.panel_rect.x + this.panel_rect.width * 435 / 1000;
            if ( this.preferences.get_pfd_adi_centered() ) {
                if ( this.panel_rect.height >= this.panel_rect.width )
                    // square or tall window : ADI is left of center
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000;
                else if ( this.panel_rect.width > this.panel_rect.height+2*instrument_size*65/1000 )
                    // ADI centered when window is wide enough (1065/1000)
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2;
                else
                    // ADI as close to the center as possible
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000 + (this.panel_rect.width-this.panel_rect.height)/2;
            } else {
                adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000;
            }
            adi_cy = panel_offset_y + this.panel_rect.y + instrument_size * 510 / 1000;
            adi_size_left = instrument_size * 250 / 1000;
            adi_size_right = instrument_size * 250 / 1000;
            adi_size_up = instrument_size * 260 / 1000;
            adi_size_down = instrument_size * 240 / 1000;
            adi_pitchscale = 22; // max scale down
            adi_pitch90 = adi_size_down * 90 / adi_pitchscale;
			adi_roundrectarea = new Area(new RoundRectangle2D.Float(
					adi_cx - adi_size_left, adi_cy - adi_size_up, adi_size_left + adi_size_right, adi_size_up + adi_size_down,
					(int)(60 * scaling_factor),
					(int)(60 * scaling_factor)));
			adi_area = new Area(new Rectangle2D.Float(adi_cx - adi_size_left, adi_cy - adi_size_up, adi_size_left + adi_size_right, adi_size_up + adi_size_down));
			adi_area.subtract(adi_roundrectarea);            
			adi_fd_bar_stroke=new BasicStroke(3.0f * scaling_factor);
			
            tape_width = instrument_size * 120 / 1000;
            speedtape_left = adi_cx - adi_size_left - (instrument_size * 50 / 1000) - tape_width;
            

            if ( instrument_style == Avionics.STYLE_AIRBUS ) {
            	// TODO : airbus_style is set in super class !!
            	airbus_style = true;
            	boeing_style = false;           	
           	
            	adi_cy = panel_offset_y + this.panel_rect.y + instrument_size * 515 / 1000;
            	// On Airbus, tape height is align with horizon
                adi_size_left = instrument_size * 250 / 1000;
                adi_size_right = instrument_size * 250 / 1000;
                adi_size_up = instrument_size * 260 / 1000;
                adi_size_down = instrument_size * 260 / 1000;
                adi_pitchscale = 22; // max scale down
                adi_pitch90 = adi_size_down * 90 / adi_pitchscale;
        		adi_airbus_horizon_area = new Area ( new Arc2D.Float ( (float) adi_cx - adi_size_left, (float) adi_cy - adi_size_up, (float) adi_size_left + adi_size_right, (float) adi_size_up + adi_size_down, 0.0f,360.0f,Arc2D.CHORD));
        		adi_square_horizon_area = new Area ( new Rectangle(adi_cx - adi_size_left*9/10, adi_cy - adi_size_up*11/10, adi_size_left*9/10 + adi_size_right*9/10, adi_size_up + adi_size_down*12/10) );
        		adi_airbus_horizon_area.intersect( adi_square_horizon_area );
        		adi_pitchmark_area = new Area ( new Rectangle(
        				adi_cx - adi_size_left + adi_size_left/16,
        				adi_cy - adi_size_up*37/48,
        				adi_size_left - adi_size_left/16 + adi_size_right - adi_size_right/16,
        				adi_size_up*37/48 + adi_size_down*37/48 ));
        		adi_hdg_bug_stroke = new BasicStroke(4.0f * grow_scaling_factor);
        		adi_fpv_stroke = new BasicStroke(2.0f * grow_scaling_factor);
        	    adi_att_flag_y = adi_cy;
        	    adi_ra_flag_y = adi_cy;
        	    adi_ra_flag_x = adi_cx;
        	    adi_fd_flag_y = adi_cy;
        	    adi_fd_flag_x  = adi_cx;
        	    adi_fpv_flag_x = adi_cx;
        		
                tape_width = instrument_size * 140 / 1000;
                speedtape_left = adi_cx - adi_size_left - (instrument_size * 30 / 1000) - tape_width;
                speed_flag_y = adi_cy + line_height_l/2;
                speed_flag_cx = speedtape_left+tape_width*1/16;
                
            	tape_height = instrument_size * 530 / 1000;
            	tape_top = adi_cy - tape_height/2;            	
                
                fma_width =  instrument_size * 980 / 1000; // full width on A320 
                fma_left = speedtape_left;
                fma_height = instrument_size * 135 / 1000;
                fma_col_1 = fma_width*100/500;
                fma_col_2 = fma_width*206/500;
                fma_col_3 = fma_width*325/500;
                fma_col_4 = fma_width*419/500;
                
                // Altimeter tape
                altitape_left = adi_cx + adi_size_right + (instrument_size * 65 / 1000);
                altitape_right = altitape_left + tape_width*60/100; 
            	alti_box_x = new int[] {
            			altitape_left - tape_width*2/50,
            			altitape_right,
            			altitape_right,
            			altitape_left + digit_width_xxl*3 + digit_width_xl * 9/4,
            			altitape_left + digit_width_xxl*3 + digit_width_xl * 9/4,
            			altitape_right,
            			altitape_right,
            			altitape_left - tape_width*2/50,
            	};
            	alti_box_y = new int[] {
            			adi_cy - line_height_xxl*6/11,
            			adi_cy - line_height_xxl*6/11,
            			adi_cy - line_height_l*12/10,
            			adi_cy - line_height_l*12/10,
            			adi_cy + line_height_l*12/10,
            			adi_cy + line_height_l*12/10,
            			adi_cy + line_height_xxl*6/11,
            			adi_cy + line_height_xxl*6/11,
            	};
            	alti_ind_rect_x = altitape_left - tape_width*2/50;
            	alti_ind_rect_y = adi_cy - line_height_xxl*6/11;
            	alti_ind_rect_w = altitape_right - altitape_left + tape_width*3/50 + 1;
            	alti_ind_rect_h = line_height_xxl*12/11;
                alti_bug_x = new int[] {
                		altitape_left - 2,
                		altitape_left - tape_width*3/24,
                		altitape_left - tape_width*3/24,
                		altitape_left + tape_width*4/20,
                		altitape_left + tape_width*4/20,
                		altitape_left - tape_width*3/24,
                		altitape_left - tape_width*3/24
                };
                /*
                int[] alti_bug_y = {
                		alt_y,
                		alt_y + pfd_gc.tape_width*2/21,
                		alt_y + pfd_gc.tape_height*2/18,
                		alt_y + pfd_gc.tape_height*2/18,
                		alt_y - pfd_gc.tape_height*2/18,
                		alt_y - pfd_gc.tape_height*2/18,
                		alt_y - pfd_gc.tape_width*2/21
                };
                */ 
                alti_ind_area = new Area ( new Polygon(alti_box_x, alti_box_y, 8) );
                alti_outside_area = new Area(new Rectangle(altitape_left - tape_width*3/24, tape_top, tape_width + tape_width*3/24, tape_height));
                alti_outside_area.subtract(new Area(alti_ind_area));
                alti_flag_cx = altitape_left + tape_width*30/100;
                alti_flag_x = altitape_left;
                alti_flag_y = adi_cy + line_height_l/2;
               
                vsi_height = instrument_size * 640 / 1000;
                vsi_width = instrument_size * 85 / 1000;
                vsi_left = altitape_left + tape_width + (instrument_size * 30 / 1000);
                vsi_top = adi_cy - vsi_height/2;
                vsi_flag_cx = vsi_left + vsi_width/5;
                vsi_flag_y = adi_cy - line_height_xxl/2 - 4;
                
            	comm_lost_y = tape_top - line_height_xxl;
            	
            } else {
            	airbus_style = false;
            	boeing_style = true;
            	
            	adi_fpv_stroke = new BasicStroke(3.0f * grow_scaling_factor);
            	adi_ils_marker_stroke = new BasicStroke(4.0f * grow_scaling_factor);
        	    adi_att_flag_y = adi_cy - instrument_size * 60 / 1000;
        	    // RA may be displayed inside the ADI or just in a circle up right
        	    // ADI bottom
        	    
        	    adi_ra_flag_y = adi_cy + instrument_size * 240 / 1000;
        	    adi_ra_flag_x = adi_cx; 
        	    
        	    adi_fd_flag_y = adi_cy - instrument_size *  100 / 1000;
        	    adi_fd_flag_x  = adi_cx + instrument_size *  170 / 1000;
        	    adi_fpv_flag_x = adi_cx - instrument_size *  165 / 1000;
        	    
                tape_height = instrument_size * 750 / 1000;
                tape_top = adi_cy - tape_height/2;
                
                speed_flag_y = adi_cy - line_height_xl/2;
                speed_flag_cx = speedtape_left+tape_width*1/16 + (tape_width*11/16)/2;
                
                fma_width = instrument_size * 560 / 1000; // was 546
                fma_left = adi_cx - fma_width/2;
                fma_height = instrument_size * 80 / 1000;
                fma_col_1 = fma_width*100/500;
                fma_col_2 = fma_width*206/500;
                fma_col_3 = fma_width*325/500;
                fma_col_4 = fma_width*419/500;
                
                // Altimeter tape
                altitape_left = adi_cx + adi_size_right + (instrument_size * 73 / 1000);
                alti_box_x = new int[]  {
                    altitape_left + tape_width*1/8,
                    altitape_left + tape_width*1/8 + tape_width*3/16,
                    altitape_left + tape_width*1/8 + tape_width*3/16,
                    altitape_left + tape_width + tape_width*35/80,
                    altitape_left + tape_width + tape_width*35/80,
                    altitape_left + tape_width*1/8 + tape_width*3/16,
                    altitape_left + tape_width*1/8 + tape_width*3/16,
                };
                alti_box_y = new int[] {
                    adi_cy,
                    adi_cy + tape_width*3/20,
                    adi_cy + line_height_xxl,
                    adi_cy + line_height_xxl,
                    adi_cy - line_height_xxl,
                    adi_cy - line_height_xxl,
                    adi_cy - tape_width*3/20
                };
                alti_flag_cx = altitape_left + tape_width*50/100;
                alti_flag_x = altitape_left;
                alti_flag_y = adi_cy - line_height_xl/2;
                
                vsi_height = instrument_size * 525 / 1000;
                vsi_width = instrument_size * 85 / 1000;
                vsi_left = altitape_left + tape_width + (instrument_size * 30 / 1000);
                vsi_top = adi_cy - vsi_height/2;
                vsi_flag_cx = vsi_left + vsi_width*3/5;
                vsi_flag_y = adi_cy - line_height_xl*9/8;
                
                comm_lost_y = adi_cy + instrument_size * 100 / 1000;
            }
            
            

            fma_top = panel_offset_y + this.panel_rect.y + instrument_size * 15 / 1000;
            dg_radius =  instrument_size * 350 / 1000;
            hsi_tick_w = dg_radius / 12;
            dg_cx = adi_cx;
            dg_cy = panel_offset_y + this.panel_rect.y + instrument_size * 880 / 1000 + dg_radius*102/100;
            if ( draw_hsi ) dg_cy += dg_radius*29/100 + line_height_xl*3/2;
            if ( this.preferences.get_pfd_adi_centered() ) {
                // if we want the ADI to be centered horizontally, it means that we will draw the ND below the PFD
                // so we can get rid of the DG or HSI
                dg_cy += 8; // whatever, but far away...
            }
            if ( instrument_style == Avionics.STYLE_AIRBUS ) dg_cy += instrument_size * 80 / 1000;
            /*
            vsi_width = instrument_size * 85 / 1000;
            vsi_left = altitape_left + tape_width + (instrument_size * 30 / 1000);

            vsi_top = adi_cy - vsi_height/2;
            */
            
            gs_width = adi_size_right * 5 / 32;
            gs_height = 2 * adi_size_down;
            cdi_width = 2 * adi_size_left;
            cdi_height = adi_size_down * 5 / 32;
            
            // RA Radio altitude indicator
            ra_r = adi_size_right / 4;
            ra_x = adi_cx + adi_size_right - ra_r;
            ra_high_y = ( ( adi_cy - adi_size_up ) + ( fma_top + fma_height ) ) / 2;
            ra_low_y = adi_cy + adi_size_down + cdi_height + ra_r/2;
    	    // RA Flag in Mins circle            
    	    adi_ra_flag_y = this.preferences.get_draw_aoa() ? ra_low_y : ra_high_y ;    	    
    	    adi_ra_flag_x = ra_x;
    	    
    	    // AOA Angle of Attack indicator
            aoa_r = ra_r;
            aoa_x = ra_x;
            aoa_y = ra_high_y;
            radios_top = fma_top;
            adi_aoa_flag_x = aoa_x;
            adi_aoa_flag_y = aoa_y + aoa_r/3;
            
            radios_width = instrument_size * 125 / 1000;
            // navradios_left = this.panel_rect.x + this.panel_rect.width/2 - instrument_size/2 - (instrument_size * 30 / 1000) - radios_width;
            navradios_left = speedtape_left - (instrument_size * 30 / 1000) - radios_width;
            // comradios_left = this.panel_rect.x + this.panel_rect.width/2 + instrument_size/2 + (instrument_size * 30 / 1000);
            comradios_left = vsi_left + vsi_width + (instrument_size * 25 / 1000);
            radios_height = instrument_size * 440 / 1000;
            
            // HDG Tape on Airbus
            hdg_top = adi_cy + instrument_size * 405 / 1000;
            hdg_left = adi_cx - adi_size_left*9/10;
            hdg_height = instrument_size * 65 / 1000;
            hdg_width = (adi_size_left + adi_size_right)*9/10;           
            hdg_diamond_shift = hdg_height/6;
            hdg_diamond_size = hdg_diamond_shift*2;           
            hdg_diamond_img = createTrackDiamond();
        	
            // ILS data on Airbus
            ils_line3 = hdg_top + hdg_width; 
            ils_line2 = ils_line3 + line_height_l;
            ils_line1 = ils_line2 + line_height_l;
            ils_x = speedtape_left;
            
        	

        }
        
        if (colors_updated || settings_updated) {
        	hdg_diamond_img = createTrackDiamond();        	
        }

    }

    private BufferedImage createTrackDiamond() {   
    	BufferedImage diamond_image = new BufferedImage(hdg_diamond_size,hdg_diamond_size*3/2,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_dmd = diamond_image.createGraphics();
    	// diamond
    	int d_d = hdg_diamond_shift - 1;
    	int diamond_x[] = {
    			hdg_diamond_shift,
    			hdg_diamond_shift + d_d,
    			hdg_diamond_shift,
    			hdg_diamond_shift - d_d
    	};
    	int diamond_y[] = {
    			1 ,
    			1 + d_d*3/2,
    			1 + 3*d_d,
    			1 + d_d*3/2	
    	};
    	g_dmd.setColor(pfd_active_color);
    	g_dmd.setStroke(new BasicStroke(2.0f * scaling_factor));
    	g_dmd.drawPolygon(diamond_x, diamond_y, 4);
    	return diamond_image;
    }

    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void setTransparent(Graphics2D g2, boolean is_transparent) {
        draw_transparent = ( is_transparent && ( this.preferences.get_pfd_dial_opacity() != 1.0f ) );
        if ( draw_transparent ) {
            orig_cmpst = g2.getComposite();
            float alpha = this.preferences.get_pfd_dial_opacity();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
        }
    }

    public void setOpaque(Graphics2D g2) {
        // should only be called after a call to setTransparent
        if ( draw_transparent ) {
            g2.setComposite(orig_cmpst);
        }
    }

}
