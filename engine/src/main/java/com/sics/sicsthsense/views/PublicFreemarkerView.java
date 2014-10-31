
/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.views;

import io.dropwizard.views.View;

import se.sics.sicsthsense.model.BaseModel;
import se.sics.sicsthsense.core.User;

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

  public PublicFreemarkerView(String templateName, T model) {
    super("/views/ftl/"+templateName);
    this.model = model;
  }

  public T getModel() {
    return model;
  }

  public User getUser() {
    return model.getUser();
  }

  public String getUserString() {
		if (model.getUser()==null) {
			return "User is null";
		}
    return model.getUser().toString();
  }

}
