package com.impronta.fichajesservice;

import javax.naming.InvalidNameException;
import javax.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class FichajesController {

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/")
	public ModelAndView home(Model model) {
		model.addAttribute("message", "Please upload a pdf file");

		System.out.println("Entra en /");
		
		return new ModelAndView("home", model.asMap());
		
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/test/{codigo}")
	@ResponseBody
	public String test(@PathVariable String codigo) {

		System.out.println("Entra en test");
		return codigo;
	}
	
	/**
	 * Metodo para firmar
	 * */
	@GetMapping(path="/sign", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Respuesta fichar(Model model, @RequestParam("file") MultipartFile file) {

		System.out.println("El archivo es "+file.getName());			
		
		if (file.isEmpty()) {
			return new Respuesta("Error");
		}

		try {

			byte[] bytes = file.getBytes();
			System.out.println("ENTRA PDFSignatureInfoParser");	
			List<PDFSignatureInfo> info = PDFSignatureInfoParser.getPDFSignatureInfo(bytes);
			System.out.println("FIN PDFSignatureInfoParser");
			System.out.println("message" + "OK");
			System.out.println("filename" + file.getOriginalFilename());
			System.out.println("pdfSignatureInfo" + info);
			
			model.addAttribute("message", "OK");
			model.addAttribute("filename", file.getOriginalFilename());
			model.addAttribute("pdfSignatureInfo", info);

		} catch (IOException | InvalidNameException | CertificateException | NoSuchAlgorithmException
				| InvalidKeyException | SignatureException | NoSuchProviderException e) {
			model.addAttribute("message", "Cannot open file: " + e.getMessage());
			e.printStackTrace();
			return new Respuesta("Error");
		}

		return new Respuesta("ok");
	}

	/**
	 * Metodo para revisar la firma
	 * */
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(path = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object usuarioGet(Model model, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
		
		try {
			System.out.println("El archivo es "+file.getName());
			
			PDFCreateSignature pdfCreateSignature = new PDFCreateSignature();
	
			System.out.println("Ha firmado");
			
			byte[] respuesta = pdfCreateSignature.sign(file.getInputStream());
			
			System.out.println("Ha terminado");
		} catch (IOException e) {
			e.printStackTrace();
			return new Respuesta("Error");
		}
		return new Respuesta("ok");

	}

}
