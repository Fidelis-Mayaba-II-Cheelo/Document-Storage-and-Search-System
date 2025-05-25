package com.example.DocumentStorageSystem1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.DocumentStorageSystem1.entity.Document;
import com.example.DocumentStorageSystem1.repository.DocumentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DocumentStorageSystem1.dto.DocumentDto;
import com.example.DocumentStorageSystem1.service.impl.DocumentServiceImpl;

import lombok.AllArgsConstructor;

@RequestMapping("/documents")
@AllArgsConstructor
@Controller
public class DocumentController {
    private DocumentServiceImpl documentService;
    private DocumentRepository documentRepository;


    @GetMapping("/addDocument")
    public String showAddDocumentForm(Model model){
        model.addAttribute("Document", new DocumentDto());
        model.addAttribute("templateName", "add_document");
        model.addAttribute("fragmentName", "add_documents_page");
        //model.addAttribute("bodyTemplate", "add_document :: add_documents_page");
        return "layout";
    }

    @PostMapping("/addDocument")
    public String addDocument(@RequestParam("title") String title,
                              @RequestParam("author") String author,
                              @RequestParam("upload_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate uploadDate,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        try {
            DocumentDto dto = new DocumentDto();
            dto.setTitle(title);
            dto.setAuthor(author);
            dto.setUpload_date(uploadDate);
            documentService.saveDocumentWithFile(dto, file);
            model.addAttribute("templateName", "add_document");
            model.addAttribute("fragmentName", "add_documents_page");
            model.addAttribute("Document", new DocumentDto());
            redirectAttributes.addFlashAttribute("SuccessMessage", "Document uploaded successfully");
        } catch (Exception e) {
            model.addAttribute("templateName", "add_document");
            model.addAttribute("fragmentName", "add_documents_page");
            redirectAttributes.addFlashAttribute("ErrorMessage", "Failed to upload document: " + e.getMessage());
        }

        return "redirect:/documents/view_documents";
    }


    // Showing the update form (Corrected)
    @GetMapping("/update/{documentId}")
    public String showUpdateDocumentForm(@PathVariable Long documentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            DocumentDto document = documentService.getSingleDocument(documentId); // Load existing task
            model.addAttribute("UpdateDocument", document); // Add it to the model
            //model.addAttribute("bodyTemplate", "update_document :: update_documents_page");
            model.addAttribute("templateName", "update_document");
            model.addAttribute("fragmentName", "update_documents_page");
            return "layout"; // Return the update form view
        } catch (RuntimeException e) {
            //model.addAttribute("bodyTemplate", "update_document :: update_documents_page");
            model.addAttribute("templateName", "update_document");
            model.addAttribute("fragmentName", "update_documents_page");
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
            return "layout"; // Or redirect to an error page
        }
    }

    @PostMapping("/update/{documentId}")
    public String addDocument(@RequestParam("title") String title,
                              @RequestParam("author") String author,
                              @RequestParam("upload_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate uploadDate,
                              @RequestParam("file") MultipartFile file,
                              @PathVariable Long documentId,
                              @ModelAttribute DocumentDto documentDto,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        try {
            DocumentDto dto = new DocumentDto();
            dto.setDoc_id(documentId);
            dto.setTitle(title);
            dto.setAuthor(author);
            dto.setUpload_date(uploadDate);
            documentService.saveDocumentWithFile(dto, file);
            model.addAttribute("templateName", "update_document");
            model.addAttribute("fragmentName", "update_documents_page");
            redirectAttributes.addFlashAttribute("SuccessMessage", "Document uploaded successfully");
        } catch (Exception e) {
            model.addAttribute("templateName", "update_document");
            model.addAttribute("fragmentName", "update_documents_page");
            redirectAttributes.addFlashAttribute("ErrorMessage", "Failed to upload document: " + e.getMessage());
            model.addAttribute("UpdateDocument", documentDto);
        }

        return "redirect:/documents/view_documents";
    }


    //Method for getting one task
    @GetMapping("/view_document")
    public String viewDocument(Model model, @RequestParam Long documentId){
        try {
            DocumentDto theDocument = documentService.getSingleDocument(documentId); // No tasksDto parameter needed
            //model.addAttribute("bodyTemplate", "view_documents :: view_documents_page");
            model.addAttribute("templateName", "view_documents");
            model.addAttribute("fragmentName", "view_documents_page");
            model.addAttribute("ViewDocument", theDocument); // Add the task to the model
            return "layout";
        } catch (RuntimeException ex) {
            //model.addAttribute("bodyTemplate", "view_documents :: view_documents_page");
            model.addAttribute("templateName", "view_documents");
            model.addAttribute("fragmentName", "view_documents_page");
            model.addAttribute("ErrorMessage", ex.getMessage()); // Add the error message to the model
            return "layout";
        }
    }


    //Method for getting all tasks
    @GetMapping("/view_documents")
    public String viewAllDocuments(Model model){
        List<DocumentDto> documentDto = documentService.getDocuments();
        model.addAttribute("ViewAllDocuments", documentDto);
        //model.addAttribute("bodyTemplate", "view_documents :: view_documents_page");
        model.addAttribute("templateName", "view_documents");
        model.addAttribute("fragmentName", "view_documents_page");
        return "layout";
    }


    //Method for deleting one task
    @GetMapping("/delete_document/{documentId}")
    public String deleteDocument(@PathVariable Long documentId, RedirectAttributes redirectAttributes){
        try{
            documentService.deleteSingleDocument(documentId);
            redirectAttributes.addFlashAttribute("SuccessMessage", "The document was deleted successfully");
            return "redirect:/documents/view_documents";
        } catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
            return "redirect:/documents/view_documents";
        }
    }

    //Method for deleting all tasks
    @GetMapping("/delete_all_documents")
    public String deleteAllDocuments(RedirectAttributes redirectAttributes){
        try{
            documentService.deleteAllDocuments();
            redirectAttributes.addFlashAttribute("SuccessMessage", "All documents have been deleted successfully");
            return "redirect:/documents/addDocument";
        } catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
            return "redirect:/documents/addDocument";
        }
    }
}
