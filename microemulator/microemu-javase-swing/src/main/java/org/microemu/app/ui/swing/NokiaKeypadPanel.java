package org.microemu.app.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.microemu.MIDletBridge;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceButtonsHelper;
import org.microemu.device.j2se.J2SEInputMethod;

/**
 * On screen Nokia style keypad. Replaces the skin image. Every JButton sends
 * the same ButtonName a real keyboard key would send, so key codes are the
 * ones defined in ButtonDetaultDeviceKeyCodes (-6 -7 -5 -1..-4 -10 -11 -8).
 */
public class NokiaKeypadPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final ButtonName[] LAYOUT = { ButtonName.SOFT1, ButtonName.UP, ButtonName.SOFT2, ButtonName.LEFT,
			ButtonName.SELECT, ButtonName.RIGHT, ButtonName.SEND, ButtonName.DOWN, ButtonName.END, ButtonName.KEY_NUM1,
			ButtonName.KEY_NUM2, ButtonName.KEY_NUM3, ButtonName.KEY_NUM4, ButtonName.KEY_NUM5, ButtonName.KEY_NUM6,
			ButtonName.KEY_NUM7, ButtonName.KEY_NUM8, ButtonName.KEY_NUM9, ButtonName.KEY_STAR, ButtonName.KEY_NUM0,
			ButtonName.KEY_POUND };

	private static final String[] LABELS = { "Soft L", "\u25B2", "Soft R", "\u25C0", "OK", "\u25B6", "Send", "\u25BC",
			"End", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#" };

	public NokiaKeypadPanel() {
		setLayout(new GridLayout(7, 3, 3, 3));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		for (int i = 0; i < LAYOUT.length; i++) {
			add(createKey(LAYOUT[i], LABELS[i]));
		}
	}

	private JButton createKey(final ButtonName name, String label) {
		JButton key = new JButton(label);
		// keep keyboard focus on the device component so PC keys still work
		key.setFocusable(false);
		key.setMargin(new java.awt.Insets(2, 2, 2, 2));
		key.setFont(key.getFont().deriveFont(Font.BOLD, 13f));
		if (name == ButtonName.SEND) {
			key.setForeground(new Color(0x00, 0x80, 0x00));
		} else if (name == ButtonName.END) {
			key.setForeground(new Color(0xC0, 0x00, 0x00));
		}
		key.addMouseListener(new MouseAdapter() {
			private J2SEButton pressed;

			public void mousePressed(MouseEvent e) {
				if (MIDletBridge.getCurrentMIDlet() == null) {
					return;
				}
				pressed = J2SEDeviceButtonsHelper.getButton(name);
				if (pressed != null) {
					input().buttonPressed(pressed, '\0');
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (pressed != null) {
					input().buttonReleased(pressed, '\0');
					pressed = null;
				}
			}
		});
		return key;
	}

	private static J2SEInputMethod input() {
		Device device = DeviceFactory.getDevice();
		return (J2SEInputMethod) device.getInputMethod();
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 230);
	}
}
