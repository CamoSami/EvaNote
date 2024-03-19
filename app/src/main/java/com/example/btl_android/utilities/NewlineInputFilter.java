package com.example.btl_android.utilities;

import android.text.InputFilter;
import android.text.Spanned;

public class NewlineInputFilter implements InputFilter {
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
	                           Spanned dest, int dstart, int dend) {
		// Iterate through the characters in the source text
		for (int i = start; i < end; i++) {
			// Check if the character is a newline character
			if (source.charAt(i) == '\n') {
				// Replace newline character with an empty string
				return "";
			}
		}
		// Return null to accept the original text
		return null;
	}
}
