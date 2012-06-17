package controllers;

import java.util.List;
import models.DataLog;
import models.EndPoint;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.logsPage;

public class Logs extends Controller {

    public static Result get(String userName, String endPointName, String path, long tail, long last, long since) {
        final User user = User.getByUserName(userName);
        if(user == null) return notFound();
        final EndPoint endPoint = EndPoint.getByLabel(user, endPointName);
        if(endPoint == null) return notFound();

        List<DataLog> dataSet;

        if(tail < 0) tail = 0L;
        if(last < 0) last = 0L;
        if(since < 0) since = 0L;
        if(tail == 0 && last == 0 && since ==0) tail = 1L; /* Default behavior: return the last item only */

        if(tail > 0) {
            dataSet = DataLog.getByEndPointTail(endPoint, (int)tail);
        } else if(last > 0) {
            dataSet = DataLog.getByEndPointLast(endPoint, last);
        } else if(since > 0) {
            dataSet = DataLog.getByEndPointSince(endPoint, since);
        } else {
            dataSet = DataLog.getByEndPoint(endPoint);
        }

        return ok(logsPage.render(endPoint, dataSet));
    }

}
