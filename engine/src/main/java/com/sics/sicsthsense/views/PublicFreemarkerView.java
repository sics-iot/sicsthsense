package com.sics.sicsthsense.views;

import com.yammer.dropwizard.views.View;

import com.sics.sicsthsense.model.BaseModel;
import com.sics.sicsthsense.core.User;

/**
 * <p>View to provide the following to resources:</p>
 * <ul>
 * <li>Representation provided by a Freemarker template with a given model</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class PublicFreemarkerView<T extends BaseModel> extends View {

  private final T model;
	public String test;

  public PublicFreemarkerView(String templateName, T model) {
    super("/views/ftl/"+templateName);
    this.model = model;
		test="this is not a test";
  }

  public T getModel() {
    return model;
  }
  public String getTest() {
    return test;
  }
  public User getUser() {
    return model.getUser();
  }
  public String getUserString() {
		if (model.getUser()==null) {
			return "user is null";
		}
    return model.getUser().toString();
  }

}
