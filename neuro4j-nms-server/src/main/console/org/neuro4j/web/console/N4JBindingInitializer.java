package org.neuro4j.web.console;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

/**
 * Shared WebBindingInitializer for IC's custom editors.
 *
 */
public class N4JBindingInitializer implements WebBindingInitializer {

	public void initBinder(WebDataBinder binder, WebRequest request) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

}
