package de.agilecoders.wicket.extensions.markup.html.bootstrap.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.FileUploadException;

import com.google.common.io.Files;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeCDNCSSReference;

/**
 * 
 * Please add
 * 
 * mountResource(SummernoteStoredImageResourceReference.SUMMERNOTE_MOUNT_PATH,
 * new SummernoteStoredImageResourceReference(new File("/Users/MyUser/"),"subpath"));
 * 
 * and then use config
 * SummernoteConfig config = new SummernoteConfig();
 * config.setSubFolderName("subpath");
 * ...
 * 
 * @author Tobias Soloschenko
 *
 */
public class SummernoteEditor extends FormComponent<String> {

    private static final long serialVersionUID = 1L;

    private SummernoteConfig config;

    private SummernoteEditorImageAjaxEventBehavior summernoteEditorImageAjaxEventBehavior;

    private class SummernoteEditorImageAjaxEventBehavior extends AbstractDefaultAjaxBehavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void respond(AjaxRequestTarget target) {
	    try {
		ServletWebRequest webRequest = (ServletWebRequest) getRequest();
		MultipartServletWebRequest multiPartRequest = webRequest.newMultipartWebRequest(
			Bytes.megabytes(config.getMaxFileSize()), "ignored");
		multiPartRequest.parseFileParts();
		List<File> storedFile = storeFile(target, multiPartRequest);
		onImageUpload(target, storedFile);
	    } catch (FileUploadException fux) {
		onImageError(target, fux);
	    }
	}

	/**
	 * Stores the submitted files
	 * 
	 * @param target
	 *            the target to apply the url to the response
	 * 
	 * @param multiPartRequest
	 *            the multi part request to get the files from
	 * @throws IOException
	 *             if an exception occured while reading / writing any file
	 */
	private List<File> storeFile(AjaxRequestTarget target, MultipartServletWebRequest multiPartRequest) {
	    List<File> files = new ArrayList<File>();
	    Map<String, List<FileItem>> fileMap = multiPartRequest.getFiles();
	    Iterator<List<FileItem>> fileItemListIterator = fileMap.values().iterator();
	    while (fileItemListIterator.hasNext()) {
		Iterator<FileItem> fileItemIterator = fileItemListIterator.next().iterator();
		while (fileItemIterator.hasNext()) {
		    try {
			FileItem fileItem = fileItemIterator.next();
			File file = new File(SummernoteConfig.getUploadFolder(config.getSubFolderName()), fileItem.getName());
			Files.write(IOUtils.toByteArray(fileItem.getInputStream()), file);
			files.add(file);
			WebResponse response = (WebResponse) target.getHeaderResponse().getResponse();
			response.setHeader("imageUrl", SummernoteStoredImageResourceReference.SUMMERNOTE_MOUNT_PATH
				+ "?image=" + Base64.encodeBase64String(file.getName().getBytes()));
		    } catch (IOException e) {
			// TODO Inform the user of failure
		    }
		}
	    }
	    return files;
	}

	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
	    super.updateAjaxAttributes(attributes);
	    attributes.setMultipart(true);
	}
    }

    public SummernoteEditor(String id) {
	this(id, null, new SummernoteConfig());
    }

    public SummernoteEditor(String id, IModel<String> model) {
	this(id, model, new SummernoteConfig());
    }

    public SummernoteEditor(String id, IModel<String> model, SummernoteConfig config) {
	super(id, model);
	this.config = Args.notNull(config, "config");
	add(summernoteEditorImageAjaxEventBehavior = new SummernoteEditorImageAjaxEventBehavior());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
	response.render(JavaScriptHeaderItem.forReference(SummernoteEditorJavaScriptReference.instance()));
	response.render(CssHeaderItem.forReference(SummernoteEditorCssReference.instance()));
	response.render(CssHeaderItem.forReference(FontAwesomeCDNCSSReference.instance()));
	PackageTextTemplate summernoteTemplate = null;
	try {
	    summernoteTemplate = new PackageTextTemplate(SummernoteEditor.class, "js/summernote_init.js");
	    config.withImageUploadCallbackUrl(summernoteEditorImageAjaxEventBehavior.getCallbackUrl().toString());
	    config.put(SummernoteConfig.Id, getMarkupId());
	    String jsonConfig = config.toJsonString();
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("summernoteconfig", jsonConfig);
	    String summernoteTemplateJavaScript = summernoteTemplate.asString(variables);
	    response.render(OnDomReadyHeaderItem.forScript(summernoteTemplateJavaScript));
	} finally {
	    IOUtils.closeQuietly(summernoteTemplate);
	}
    }

    protected void onSubmit(AjaxRequestTarget target) {
	// TODO receive text on submit in form component ajax behavior
    }

    public void onImageUpload(AjaxRequestTarget target, List<File> files) {
	// NOOP
    }

    public void onImageError(AjaxRequestTarget target, FileUploadException fux) {
	// NOOP
    }
}
