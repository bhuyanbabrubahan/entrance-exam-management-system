package com.jee.publicapi.helper;

import java.awt.Font;

import org.springframework.stereotype.Component;

import com.github.cage.GCage;
@Component  // This makes the class a Spring bean
public class CustomFontCage extends GCage {
	private static final Font CUSTOM_FONT = new Font("Times New Roman", Font.BOLD, 48);

    public Font getFont() {
        return CUSTOM_FONT;
    }
}
