/**
* AOA.java
* 
* Angle of Attack indicator
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014,2022  Nicolas Carel
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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sourceforge.xhsi.XHSIStatus;

// import java.util.logging.Logger;

import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.util.FramedElement.FE_Color;



public class AOA extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    PFDFramedElement failed_aoa_flag;
    DecimalFormat deg_formatter;
    DecimalFormatSymbols format_symbols;
    
    public AOA(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        failed_aoa_flag = new PFDFramedElement(PFDFramedElement.AOA_FLAG, 0, hsi_gc, FE_Color.CAUTION);
        failed_aoa_flag.setFrameOptions(true, false, false, FE_Color.CAUTION);
        failed_aoa_flag.disableFlashing();
        deg_formatter = new DecimalFormat("0.0");
        format_symbols = deg_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        deg_formatter.setDecimalFormatSymbols(format_symbols);
    }


    public void paint(Graphics2D g2) {
    	if ( pfd_gc.powered && pfd_gc.boeing_style && this.preferences.get_draw_aoa() ) {
    		drawAOAMarks(g2);
    		if ( XHSIStatus.receiving ) {
    			drawAOA(g2);
    		} else {
    			failed_aoa_flag.setText("AOA", FE_Color.CAUTION);
    			failed_aoa_flag.paint(g2);
    		}
    	}
    }


    private void drawAOA(Graphics2D g2) {

        float aoa = this.aircraft.aoa();

        if ( this.aircraft.on_ground() && ( this.aircraft.ground_speed() < this.aircraft.get_Vso()*2/3 ) ) {
            // fixed at 0.0 when on the ground and ground speed is less than 2/3*Vso
            aoa = 0.0f;
        }

        int aoa_r = pfd_gc.aoa_r;
        int aoa_x = pfd_gc.aoa_x;
        int aoa_y;
//        if ( this.preferences.get_draw_fullwidth_horizon() && ! pfd_gc.draw_hsi ) {
//            aoa_y = pfd_gc.tape_top + aoa_r*9/8;
//        } else {
            aoa_y = pfd_gc.aoa_y;
//        }

        drawAOAMarks(g2);    
            
        g2.setColor(pfd_gc.markings_color);
        AffineTransform original_at = g2.getTransform();
        float aoa_line = aoa;
        aoa_line = Math.min(aoa_line, 21.0f);
        aoa_line = Math.max(aoa_line, -6.0f);
        g2.rotate(Math.toRadians(-aoa_line*9.0f - 45.0f), aoa_x, aoa_y);
        g2.drawLine(aoa_x, aoa_y+aoa_r-1, aoa_x, aoa_y);
        g2.setTransform(original_at);

        String aoa_str = deg_formatter.format(aoa);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(aoa_str, aoa_x - pfd_gc.get_text_width(g2, pfd_gc.font_s, aoa_str) - pfd_gc.digit_width_s/2, aoa_y + pfd_gc.line_height_s);
    }

    private void drawAOAMarks(Graphics2D g2) {
        g2.setColor(pfd_gc.markings_color);
        g2.drawArc(pfd_gc.aoa_x-pfd_gc.aoa_r, pfd_gc.aoa_y-pfd_gc.aoa_r, 2*pfd_gc.aoa_r, 2*pfd_gc.aoa_r, -90, 225);
        AffineTransform original_at = g2.getTransform();
        for ( int i=0; i<6; i++) {
            if ( i == 1 ) {
                // a longer tick mark for 0.0°
                g2.drawLine(pfd_gc.aoa_x, pfd_gc.aoa_y + pfd_gc.aoa_r*325/275, pfd_gc.aoa_x, pfd_gc.aoa_y + pfd_gc.aoa_r*225/275);
            } else {
                g2.drawLine(pfd_gc.aoa_x, pfd_gc.aoa_y + pfd_gc.aoa_r - 1, pfd_gc.aoa_x, pfd_gc.aoa_y + pfd_gc.aoa_r*225/275);
            }
            g2.rotate(Math.toRadians(-45), pfd_gc.aoa_x, pfd_gc.aoa_y);
        }
        g2.setTransform(original_at);
    }
}
