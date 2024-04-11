package edu.ucsf.mousedatabase.rest;

import java.io.File;
import java.io.FileOutputStream;
//import java.io.IOException;
import java.io.InputStream;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.DBConnect;



@Path("/upload")
@Component
public class JerseyReciever {
	@Path("/files")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFiles2(@DefaultValue("")
			@FormDataParam("files") List<FormDataBodyPart> bodyParts,
	
			@FormDataParam("MouseID") String mouseID){
		
		ArrayList<File> files = new ArrayList<File>();
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
				
				byte[] buffer = new byte[input.available()];
	            int bytesRead = -1;
	 
	            while ((bytesRead = input.read(buffer)) != -1) {
	                output.write(buffer, 0, bytesRead);
	            }
	            output.close();
	            files.add(file);
			} catch (Exception e){
				///
			}
						
			//saveFile(bodyPartEntity.getInputStream(), fileName);			
		}
		//System.out.println("about to send files");
		Log.Info("about to send files");
		DBConnect.sendFilesToDatabase(files, mouseID, "approved");
		
		return Response.ok("Jersey Recieved").build();
	}

}

