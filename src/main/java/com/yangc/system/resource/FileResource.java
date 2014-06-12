package com.yangc.system.resource;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.system.service.FileService;

@Path("/file")
public class FileResource {

	private static final Logger logger = Logger.getLogger(FileResource.class);

	private FileService fileService;

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(FormDataMultiPart form) {
		logger.info("upload");
		try {
			FormDataBodyPart part = form.getField("file");
			ContentDisposition content = part.getContentDisposition();
			InputStream in = part.getValueAs(InputStream.class);
			this.fileService.upload(in, content.getFileName(), form.getField("test").getValue());
			return Response.ok(new ResultBean(true, "上传成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

}
