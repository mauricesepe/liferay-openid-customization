package SAGovFormField.portlet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dhaval Desai
 * This class is used to prepopulate details into the prebooking form for logged-in user.
 */
@Component(
    immediate = true, 
    property = { 
        "ddm.form.field.type.name=text", 
        "service.ranking:Integer=101" 
    }, 
    service = {
        DDMFormFieldTemplateContextContributor.class 
    }
)
public class SAGovDDMFormField implements DDMFormFieldTemplateContextContributor {

    @Override
    public Map<String, Object> getParameters(DDMFormField ddmFormField,
            DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("autocompleteEnabled", isAutocompleteEnabled(ddmFormField));
        parameters.put("displayStyle", getDisplayStyle(ddmFormField));
        parameters.put("placeholder", getPlaceholder(ddmFormField, ddmFormFieldRenderingContext));

        String predefinedValue = getPredefinedValue(ddmFormField, ddmFormFieldRenderingContext);

        if (predefinedValue != null) {
            parameters.put("predefinedValue", predefinedValue);
        }

        String value = getValue(ddmFormFieldRenderingContext);

        if (Validator.isNotNull(value)) {
            parameters.put("value", value);
        }

        /** Customization Starts - Populate form attributes on page load **/
        prepopulateFormFields(ddmFormField, ddmFormFieldRenderingContext, parameters);
        /** Customization Ends **/

        parameters.put("tooltip", getTooltip(ddmFormField, ddmFormFieldRenderingContext));

        return parameters;
    }

    /**
     * This is used to prepopulated form field values.
     * 
     * @param ddmFormField
     * @param ddmFormFieldRenderingContext
     * @param parameters
     */
    private void prepopulateFormFields(DDMFormField ddmFormField,
            DDMFormFieldRenderingContext ddmFormFieldRenderingContext, Map<String, Object> parameters) {
        
        ThemeDisplay themeDisplay = (ThemeDisplay) ddmFormFieldRenderingContext.getHttpServletRequest()
                .getAttribute(WebKeys.THEME_DISPLAY);

        if (ddmFormField.getName().contains("License_Number")) {
            String licenseNbr = (String) themeDisplay.getRequest().getSession().getAttribute("License_Number");
            parameters.put("value", licenseNbr);
        }

        if (ddmFormField.getName().contains("Medicare_Number")) {
            String medicareNbr = (String) themeDisplay.getRequest().getSession().getAttribute("Medicare_Number"); 
            parameters.put("value", medicareNbr);
        }
    }
    
    /**
     * This is used to get display style.
     * 
     * @param ddmFormField
     * @return
     */
    protected String getDisplayStyle(DDMFormField ddmFormField) {
        return GetterUtil.getString(ddmFormField.getProperty("displayStyle"), "singleline");
    }

    /**
     * This is used to get placeholder of the form field.
     * 
     * @param ddmFormField
     * @param ddmFormFieldRenderingContext
     * @return
     */
    protected String getPlaceholder(DDMFormField ddmFormField,
            DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        LocalizedValue placeholder = (LocalizedValue) ddmFormField.getProperty("placeholder");

        return getValueString(placeholder, ddmFormFieldRenderingContext.getLocale(), ddmFormFieldRenderingContext);
    }

    /**
     * This is used to get predefined value of the form field.
     * 
     * @param ddmFormField
     * @param ddmFormFieldRenderingContext
     * @return
     */
    protected String getPredefinedValue(DDMFormField ddmFormField,
            DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

        if (predefinedValue == null) {
            return null;
        }

        String predefinedValueString = predefinedValue.getString(ddmFormFieldRenderingContext.getLocale());

        if (ddmFormFieldRenderingContext.isViewMode()) {
            predefinedValueString = HtmlUtil.extractText(predefinedValueString);
        }

        return predefinedValueString;
    }

    /**
     * This is used to get tooltip of the form field.
     * 
     * @param ddmFormField
     * @param ddmFormFieldRenderingContext
     * @return
     */
    protected String getTooltip(DDMFormField ddmFormField, DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        LocalizedValue tooltip = (LocalizedValue) ddmFormField.getProperty("tooltip");

        return getValueString(tooltip, ddmFormFieldRenderingContext.getLocale(), ddmFormFieldRenderingContext);
    }

    protected String getValue(DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        String value = String.valueOf(ddmFormFieldRenderingContext.getProperty("value"));

        if (ddmFormFieldRenderingContext.isViewMode()) {
            value = HtmlUtil.extractText(value);
        }

        return value;
    }

    /**
     * This is used to get value of the form field.
     * 
     * @param value
     * @param locale
     * @param ddmFormFieldRenderingContext
     * @return
     */
    protected String getValueString(Value value, Locale locale,
            DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

        if (value == null) {
            return StringPool.BLANK;
        }

        String valueString = value.getString(locale);

        if (ddmFormFieldRenderingContext.isViewMode()) {
            valueString = HtmlUtil.extractText(valueString);
        }

        return valueString;
    }

    /**
     * This is used to get autocomplete flag of the form field.
     * 
     * @param ddmFormField
     * @return
     */
    protected boolean isAutocompleteEnabled(DDMFormField ddmFormField) {
        return GetterUtil.getBoolean(ddmFormField.getProperty("autocomplete"));
    }

    @Reference
    protected DDMFormFieldOptionsFactory ddmFormFieldOptionsFactory;

    @Reference
    protected JSONFactory jsonFactory;
}
