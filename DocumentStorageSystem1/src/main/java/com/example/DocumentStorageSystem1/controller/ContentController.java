package com.example.DocumentStorageSystem1.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.DocumentStorageSystem1.dto.ContentDto;
import com.example.DocumentStorageSystem1.service.impl.ContentServiceImpl;
import com.example.DocumentStorageSystem1.service.impl.DocumentServiceImpl;

import lombok.AllArgsConstructor;

@RequestMapping("/contents")
@Controller
@AllArgsConstructor
public class ContentController {

    private final ContentServiceImpl contentService;
    private final DocumentServiceImpl documentService;

    @GetMapping("/addContent")
    public String showAddContentForm(Model model) {
        model.addAttribute("Content", new ContentDto());
        //List<DocumentDto> allDocuments = documentService.getDocuments();
        model.addAttribute("allDocuments", documentService.getDocuments());
        //model.addAttribute("bodyTemplate", "add_content :: add_content_page");
        model.addAttribute("templateName", "add_content");
        model.addAttribute("fragmentName", "add_content_page");
        return "layout";
    }

    @PostMapping("/addContent")
    public String addContent(@ModelAttribute ContentDto contentDto, RedirectAttributes redirectAttributes, Model model) {
        try {
            contentService.addContentToDocuments(contentDto);
            //model.addAttribute("bodyTemplate", "add_content :: add_content_page");
            redirectAttributes.addFlashAttribute("SuccessMessage", "Content added Successfully");
            model.addAttribute("Content", new ContentDto());
            return "redirect:/contents/addContent";
        } catch (RuntimeException e) {
            //model.addAttribute("bodyTemplate", "add_content :: add_content_page");
            model.addAttribute("Content", new ContentDto());
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
            return "redirect:/contents/addContent";
        }
        
    }




    @GetMapping("/update/{contentId}/{documentId}")
    public String showUpdateContentForm(@PathVariable Long contentId, @PathVariable Long documentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            ContentDto content = contentService.getSingleContent(contentId, documentId);
            model.addAttribute("allDocuments", documentService.getDocuments());
            model.addAttribute("UpdateContent", content);
            //model.addAttribute("bodyTemplate", "update_content :: update_content_page");
            model.addAttribute("templateName", "update_content");
            model.addAttribute("fragmentName", "update_content_page");
            return "layout";
        } catch (RuntimeException e) {
            model.addAttribute("templateName", "update_content");
            model.addAttribute("fragmentName", "update_content_page");
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
            model.addAttribute("allDocuments", documentService.getDocuments());
            return "layout";
        }
    }

    @PostMapping("/update/{contentId}/{documentId}")
    public String updateContent(@ModelAttribute ContentDto contentDto, @PathVariable Long contentId, @PathVariable Long documentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            contentDto.setId(contentId);
            contentDto.setDoc_id(documentId);
            contentService.updateDocumentsContent(contentId, contentDto);
            redirectAttributes.addFlashAttribute("SuccessMessage", "Content updated successfully");
            return "redirect:/contents/view_contents";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("ErrorMessage", ex.getMessage());
            contentDto.setId(contentId);
            contentDto.setDoc_id(documentId);
            model.addAttribute("UpdateContent", contentDto);
            //model.addAttribute("bodyTemplate", "update_content :: update_content_page");
            model.addAttribute("templateName", "update_content");
            model.addAttribute("fragmentName", "update_content_page");
            model.addAttribute("allDocuments", documentService.getDocuments());
            return "layout";
        }
    }

    @GetMapping("/view_content")
    public String viewContent(Model model, @RequestParam Long contentId, @RequestParam Long documentId) {
        try {
            ContentDto content = contentService.getSingleContent(contentId, documentId);
            model.addAttribute("ViewContent", content);
            model.addAttribute("templateName", "view_content");
            model.addAttribute("fragmentName", "view_content_page");
            return "layout";
        } catch (RuntimeException ex) {
            model.addAttribute("templateName", "view_content");
            model.addAttribute("fragmentName", "view_content_page");
            model.addAttribute("ErrorMessage", ex.getMessage());
            return "layout";
        }
        
    }

    @GetMapping("/view_contents")
    public String viewAllContents(Model model) {
        List<ContentDto> contentDtoList = contentService.getDocumentContents();
        model.addAttribute("ViewAllContents", contentDtoList);
        //model.addAttribute("bodyTemplate", "view_content :: view_content_page");
        model.addAttribute("templateName", "view_content");
        model.addAttribute("fragmentName", "view_content_page");
        return "layout";
    }

    @GetMapping("/delete_content/{contentId}/{documentId}")
    public String deleteContent(@PathVariable Long contentId, @PathVariable Long documentId, RedirectAttributes redirectAttributes) {
        try {
            contentService.deleteSingleContentForDocument(contentId, documentId);
            redirectAttributes.addFlashAttribute("SuccessMessage", "The content was deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
        }
        return "redirect:/contents/view_contents";
    }


    @GetMapping("/delete_all_contents")
    public String deleteAllContents(@RequestParam Long documentId, RedirectAttributes redirectAttributes) {
        try {
            contentService.deleteAllContentForDocument(documentId);
            redirectAttributes.addFlashAttribute("SuccessMessage", "All contents have been deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("ErrorMessage", e.getMessage());
        }
        return "redirect:/contents/view_contents";
    }
}
