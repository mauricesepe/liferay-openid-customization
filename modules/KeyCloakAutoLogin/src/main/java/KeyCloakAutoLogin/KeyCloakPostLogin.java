package KeyCloakAutoLogin;

import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceHandler;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"key=login.events.post"
	},
	service = LifecycleAction.class
)

/**
 * @author Maurice Sepe
 * This class is used to retrieve claim values from the openID token.
 */
public class KeyCloakPostLogin implements LifecycleAction {
    
    @Reference
	private OpenIdConnectServiceHandler _openIdConnectServiceHandler;

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
		throws ActionException {

        HttpSession httpSession = lifecycleEvent.getRequest().getSession(false);
        OpenIdConnectSession openIdConnectSession = 
                (OpenIdConnectSession) httpSession.getAttribute(
                        OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION);

        if (null != openIdConnectSession) {
            String licenseNum = openIdConnectSession.getUserClaim("License_Number");
            httpSession.setAttribute("License_Number", licenseNum);
            String medicareNum = openIdConnectSession.getUserClaim("Medicare_Number");
            httpSession.setAttribute("Medicare_Number", medicareNum);
        } else {
            System.out.println("Open id connect session is null: ");
        }
    }
}