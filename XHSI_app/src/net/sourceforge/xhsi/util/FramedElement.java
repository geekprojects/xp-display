/**
* FramedElement.java
* 
* Manage framing and flashing elements for display units : ND, PFD, ...
* 
* Copyright (C) 2023 Nicolas Carel
* 
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

package net.sourceforge.xhsi.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;

public class FramedElement {

	public enum FE_Align { LEFT, CENTER, RIGHT };
	public enum FE_Orientation { HORIZONTAL, VERTICAL, MIXED };
	public enum FE_Style { ONE_LINE, ONE_LINE_LR, TWO_LINES, THREE_LINES, TWO_LINES_LR, TWO_COLUMNS };
	public enum FE_FontSize { SMALL, NORMAL, LARGE, XL, XXL };
	public enum FE_Color { MARK, ACTIVE, ARMED, MANAGED, CAUTION, ALARM };
	
	protected boolean framed ;  // True if framed
	protected boolean framing;  // True if framing active
	protected boolean cleared;  // True if there is nothing to be display
	protected boolean flashing; // True text should flash when displayed
	protected boolean flash;    // True text is flashing
	protected boolean frame_flashing; // True if frame should flash when displayed
	protected boolean frame_flash;    // True if frame is flashing
	protected boolean frame_delayed;  // True if frame is disabled after framed_milli delay (default 10s)
	
	protected boolean display_text;   // True if text is paint according to text delay and flashing
	protected boolean display_frame;  // True if frame is paint according to frame delay and flashing
	
	protected int line_height;
	protected int digit_width;
	
	// private String str_line3_right;
	protected long paint_start;	
	protected long framed_milli;		
	protected long flashed_milli;
	protected long frame_flashed_milli;
	
	protected long reconfigured_timestamp=0;
	
	protected int frame_x;
	protected int frame_y;
	protected int frame_w;
	protected int frame_h;
	protected int text_x;
	protected int text_y[] = new int[4];
	protected int text_c;
	protected int text_w;
	protected Font text_font;
	
	protected FE_FontSize font_size;
	protected FE_Color text_color;
	protected FE_Color value_color;
	protected FE_Color frame_color;
	protected FE_Style text_style;
	protected FE_Align text_align;
	protected FE_Orientation text_orientation;
    
	protected int raw; // FMA Raw
	protected int col; // FMA Column
	protected String str_line1_left;
	protected String str_line1_right;
	protected String str_line2_left;
	protected String str_line2_right;
	protected String str_line3_left;
	protected String str_line3_right;
	
    private GraphicsConfig gc;
    
	public FramedElement(GraphicsConfig gc) {
		this.gc = gc;
		framed = false;
		framing = true;
		cleared = true;
		flashing = false;
		flash = false;
		frame_flashing = false;
		frame_flash = false;
		frame_delayed = true;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";
		str_line3_left = "";
		col = 1;
		raw = 1;		
		paint_start = 0;
		framed_milli = 10000;
		flashed_milli = 10000;
		frame_flashed_milli = 10000;
		text_color = FE_Color.MARK;
		value_color = FE_Color.ARMED;
		frame_color = FE_Color.MARK;
		text_style = FE_Style.ONE_LINE;
		text_align = FE_Align.CENTER;
		font_size = FE_FontSize.XL;
		text_orientation = FE_Orientation.HORIZONTAL;
		
		// Default values, before the first call to update_config
		line_height = gc.line_height_xl;
		digit_width = gc.digit_width_xl;
		frame_x = 1;
		frame_y = 1;
		frame_h = 1;
		frame_w = 1;
		text_x = 1;
		text_y[0] = 1;
		text_y[1] = 1;
		text_y[2] = 1;
		text_y[3] = 1;
		text_c = 1;
		text_w = 1;
	}

	/**
	 * Paint the frame and prepare text painting
	 * This method must be override beginning with super.paint(g2).
	 * paint method must draw the text
	 * frame is paint before
	 * @param g2: Graphic context
	 */
	public void paint(Graphics2D g2) {    	 
		// Check GraphicConfig reconfiguration timestamp
		if (gc.reconfigured_timestamp > this.reconfigured_timestamp ) update_config(g2);

		// Compute frame display when delay applies, true if frame must be displayed
		if (framed) {
			if (gc.current_time_millis > paint_start + framed_milli ) framed = false;
		}
		
		// Compute flash, true is the element must be displayed while flashing 
		if (flash) {
			if (gc.current_time_millis > paint_start + flashed_milli ) flash = false;    		 
		}
		
		display_text = (!cleared) && ((flash && flashing) ? ((gc.current_time_millis % 1000) < 500) : true);    		 
		display_frame = (frame_flash && frame_flashing) ? ((gc.current_time_millis % 1000) < 500) : true;
		// TODO: No frame flashing - condition to be merged with previous line
		display_frame = (!cleared) && (((framed || !frame_delayed) && framing));
		
		if (display_frame) drawFrame(g2); 
		
		// Set text font and color
        setTextColor(g2);
        g2.setFont(text_font);
	}
	
	/**
	 * Return the text width regarding text orientation
	 * @param g2: Graphic context
	 * @return: int text width
	 */
    protected int textWidth(Graphics2D g2) {
    	if (text_orientation==FE_Orientation.HORIZONTAL) {
        	return gc.get_text_width(g2, text_font, str_line1_left);
    	} else {
    		return digit_width;
    	}
    }
    
	/**
	 * Return the text width regarding text orientation
	 * @param g2: Graphic context
	 * @param text: text to measure
	 * @return: int text width
	 */
    protected int textWidth(Graphics2D g2, String text) {
    	if (text_orientation==FE_Orientation.HORIZONTAL) {
        	return gc.get_text_width(g2, text_font, text);
    	} else {
    		return digit_width;
    	}
    }
    
    /**
     * Return the text height regarding text orientation
     * @param g2: Graphic context
     * @return: int text height
     */
    protected int textHeight(Graphics2D g2) {
    	if (text_orientation==FE_Orientation.HORIZONTAL) {
    		return line_height;
    	} else {
        	return str_line1_left.length() * line_height;
    	}
    }
    
    /**
     * Return the text heigth regarding text orientation
     * @param g2: Graphic context
     * @param text: text to measure
     * @return: int text heigth
     */
    protected int textHeight(Graphics2D g2, String text) {
    	if (text_orientation==FE_Orientation.HORIZONTAL) {
    		return line_height;
    	} else {
        	return text.length() * line_height;
    	}
    }
    
    /**
     * Force update config before next paint
     */
    protected void update_config() {
    	this.reconfigured_timestamp=0;
    }
    
    /**
     * Update elements positions and sizes
     * @param g2: Graphic context
     */
    protected void update_config(Graphics2D g2) {
    	reconfigured_timestamp = gc.current_time_millis;
    	
        switch (font_size) {
    		case SMALL :  text_font = gc.font_s;      line_height = gc.line_height_s;      digit_width = gc.digit_width_s;      break;
    		case NORMAL : text_font = gc.font_normal; line_height = gc.line_height_normal; digit_width = gc.digit_width_normal; break;
    		case LARGE :  text_font = gc.font_l;      line_height = gc.line_height_l;      digit_width = gc.digit_width_l;      break;
    		case XL :     text_font = gc.font_xl;     line_height = gc.line_height_xl;     digit_width = gc.digit_width_xl;     break;
    		case XXL :    text_font = gc.font_xxl ;   line_height = gc.line_height_xxl;    digit_width = gc.digit_width_xxl;    break;
    		default :     text_font = gc.font_normal; line_height = gc.line_height_normal; digit_width = gc.digit_width_normal;
        }
        
        // Default frame size
    	frame_w = textWidth(g2) + digit_width*11/10;
    	frame_h = textHeight(g2) + line_height*2/12;
    }
    
    public void setTwoColumns ( ) {
    	text_style = FE_Style.TWO_COLUMNS;
    }
    
    public void setFrameColor(FE_Color color) {
    	frame_color = color;
    }
    
    public void setFrame() {
    	framed = true;
		paint_start = gc.current_time_millis; 
    }
    
    public void setFrameFlash() {
    	frame_flash = true;
		paint_start = gc.current_time_millis; 
    }
    
    public void setFlash() {
    	flash = true;
		paint_start = gc.current_time_millis; 
    }
    
    public void clearFrame() {
    	framed = false;
    }  
    
    public void clearFrameFlash() {
    	frame_flash = false;
    }
      
    public void clearFlash() {
    	flash = false;
    } 
    
    public void enableFraming() {
    	framing = true;
    }
    
    public void disableFraming() {
    	framing = false;
    }
    
    public void enableFlashing() {
    	flashing = true;
    }

    public void disableFlashing() {
    	flashing = false;
    }

    public void enableFrameFlashing() {
    	frame_flashing = true;
    }

    public void disableFrameFlashing() {
    	frame_flashing = false;
    }
    
    public void enableFrameDelayed() {
    	frame_delayed = true;
    }
    
    public void disableFrameDelayed() {
    	frame_delayed = false;
    }
    
    /*
    public void setBigFont(boolean new_font_status) {
    	big_font = new_font_status;
    	this.update_config();
    }
    */
    
    public void setFontSize(FE_FontSize size) {
    	font_size = size;
    	this.update_config();
    }
    
    
    public void setTextOrientation(FE_Orientation orientation) {
    	text_orientation = orientation;
    }
    
    public void setFrameOptions(boolean frame_enabled, boolean delayed, boolean flashing, FE_Color color) {
    	framing = frame_enabled;
    	frame_delayed = delayed;
    	frame_flashing = flashing;
    	frame_color = color;
    }
    
    public void clearText ( ) {    	
    	framed = false;
    	cleared = true;
    	flash = false;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";   	   	
    }
    
    protected Color getColor(FE_Color fe_color) {
    	Color color = gc.pfd_alarm_color;
        switch (fe_color) {
    		case MARK:  	color = gc.pfd_markings_color; 	break;
    		case ACTIVE: 	color = gc.pfd_active_color; 	break;
    		case ARMED: 	color = gc.pfd_armed_color; 	break;
    		case CAUTION:   color = gc.pfd_caution_color; 	break;
    		case MANAGED:   color = gc.pfd_managed_color; 	break;
    		case ALARM: 	color = gc.pfd_alarm_color; 	break;    		    		
        }	
        return color;
    }
    
    protected void setTextColor(Graphics2D g2) {
   		g2.setColor(getColor(text_color));        	
    }

    protected void setValueColor(Graphics2D g2) {
   		g2.setColor(getColor(value_color)); 
    }

    protected void drawFrame(Graphics2D g2) {
    	g2.setColor(getColor(frame_color));        	        
        g2.drawRect(frame_x, frame_y, frame_w, frame_h);     	
    }
}
