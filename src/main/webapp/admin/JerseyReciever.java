//package edu.ucsf.mousedatabase.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import edu.ucsf.mousedatabase.Log;


@Path("/upload")

@Component
public class FileUploadResource {
	@Path("/files")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFiles2(@DefaultValue("")
			@FormDataParam("files") List<FormDataBodyPart> bodyParts,
			//@FormDataParam("files") FormDataContentDisposition fileDispositions)
			@FormDataParam("MouseID") String mouseID){
		
		ArrayList<File> files;
		Log.Info("reached jerseyReciever");
		
		for (int i = 0; i < bodyParts.size(); i++) {
			/*
			 * Casting FormDataBodyPart to BodyPartEntity, which can give us
			 * InputStream for uploaded file
			 */
			BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyParts.get(i).getEntity();
			String fileName = bodyParts.get(i).getContentDisposition().getFileName();
			
			try {
				File file = new File(fileName);
				InputStream input = bodyPartEntity.getInputStream();
				FileOutputStream output = new FileOutputStream(file);
				
				byte[] buffer = new byte[BUFFER_SIZE];
	            int bytesRead = -1;
	 
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	            files.append(file);
			} catch (Exception e){
				///
			}
						
			//saveFile(bodyPartEntity.getInputStream(), fileName);			
		}
		sendFilesToDatabase(files, mouseID);
		
	}

}

