package com.needbackend_app.needapp.globalconfig;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import com.needbackend_app.needapp.user.customer.service.RecordIndexerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final RecordIndexerService indexer;

    @Override
    public void run(String... args) {

        // 1. Seed initial data if database is empty
        if (categoryRepository.count() == 0) {

            // === Cleaning Category ===
            Category cleaning = new Category();
            cleaning.setName("Cleaning and maintenance");
            cleaning.setDescription("All types of cleaning");
            cleaning = categoryRepository.save(cleaning); // Save and get ID

            SubCategory residential = new SubCategory();
            residential.setName("Residential cleaning");
            residential.setDescription("House cleaning service");
            residential.setCategory(cleaning);

            SubCategory office = new SubCategory();
            office.setName("Office cleaning");
            office.setDescription("Office and janitorial cleaning service");
            office.setCategory(cleaning);

            subCategoryRepository.saveAll(List.of(residential, office));

            // == Tutoring Category ==
            Category tutoring = new Category();
            tutoring.setName("Private Lesson Tutor/ school teacher ");
            tutoring.setDescription("Tutoring service for students");
            tutoring = categoryRepository.save(tutoring); // Save and get ID

            SubCategory maths = new SubCategory();
            maths.setName("Mathematics");
            maths.setDescription("Private Maths tutor");
            maths.setCategory(tutoring);

            SubCategory english = new SubCategory();
            english.setName("English");
            english.setDescription("Private English tutor");
            english.setCategory(tutoring);

            subCategoryRepository.saveAll(List.of(maths, english));

            System.out.println("Default categories and subcategories saved to the database.");
        }

        // 2. Reindex ALL categories and subcategories (new or existing)
        List<Category> allCategories = categoryRepository.findAll();
        List<SubCategory> allSubCategories = subCategoryRepository.findAll();

        for (Category category : allCategories) {
            indexer.indexCategory(new CategoryDocumentDTO(
                    category.getId(),
                    category.getName(),
                    category.getDescription()
            ));
        }

        for (SubCategory sub : allSubCategories) {
            indexer.indexSubCategory(new SubCategoryDocumentDTO(
                    sub.getId(),
                    sub.getName(),
                    sub.getDescription(),
                    sub.getCategory().getId()
            ));
        }

        System.out.println("All categories and subcategories indexed into Elasticsearch.");
    }
}
