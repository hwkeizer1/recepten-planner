package nl.recipes.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;

@Slf4j
@Service
public class BackupService {

	private static final String TAGS_PLAN = "tags.plan";

	private final TagService tagService;
	private final ObjectMapper objectMapper;

	public BackupService(TagService tagService) {
		this.tagService = tagService;
		this.objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	public void restore(String directoryPath) {
		String tags = readTagsFromFile(directoryPath);
		restoreTags(tags);
	}

	private String readTagsFromFile(String directoryPath) {
		File tagFile = new File(directoryPath, TAGS_PLAN);

		try (BufferedReader reader = new BufferedReader(new FileReader(tagFile))) {
			return reader.readLine();
		} catch (IOException ex) {
			log.error("Fout bij het lezen van de backup file " + TAGS_PLAN);
		}
		return null;
	}

	private void restoreTags(String tags) {
		try {
			List<Tag> tagList = objectMapper.readValue(tags, new TypeReference<List<Tag>>() {});
			for (Tag tag : tagList) {
				createTag(tag);
			}
		} catch (JsonProcessingException ex) {
			log.error("Fout bij het terugzetten van de backup file " + TAGS_PLAN);
		}
	}

	private void createTag(Tag tag) {
		try {
			tagService.create(tag);
		} catch (AlreadyExistsException ex) {
			log.error("Tag {} already exists", tag.getName());
		}
	}

}
