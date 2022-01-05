package nl.recipes.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.TagRepository;
import nl.recipes.util.TestData;

class TagServiceTest {

	private static final String NEWTAG = "Newtag";

	@Mock
	TagRepository tagRepository;
	
	@InjectMocks
	TagService tagService;
	
	TestData testData;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		testData = new TestData();
		tagService.setObservableTagList(testData.getTagList());
	}
	
	@Test
	void testGetReadonlyTagList() {
		List<Tag> expectedList = testData.getTagList();
		
		assertEquals(expectedList, tagService.getReadonlyTagList());
	}
	
	@Test
	void testFindByName_HappyPath() {
		Optional<Tag> expectedTag = Optional.of(testData.getTag(3L, "Feestelijk"));
		
		assertEquals(expectedTag,  tagService.findByName("Feestelijk"));
	}
	
	@Test
	void testFindByName_NotFound() {
		Optional<Tag> expectedTag = Optional.empty();
		
		assertEquals(expectedTag,  tagService.findByName("Feestelij"));
	}
	
	@Test
	void testFindById_HappyPath() {
		Optional<Tag> expectedTag = Optional.of(testData.getTag(3L, "Feestelijk"));
		
		assertEquals(expectedTag,  tagService.findById(3L));
	}

	@Test
	void testFindById_NotFound() {
		Optional<Tag> expectedTag = Optional.empty();
		
		assertEquals(expectedTag,  tagService.findById(3000L));
	}
	
	@Test 
	void testCreate_HappyPath() throws Exception {
		Tag tag = new Tag();
		tag.setName(NEWTAG);
		Tag savedTag = testData.getTag(5L, NEWTAG);
		when(tagRepository.save(tag)).thenReturn(savedTag);
		
		assertEquals(savedTag, tagService.create(tag));
		assertEquals(5, tagService.getReadonlyTagList().size());
		assertEquals(Optional.of(savedTag), tagService.findByName(NEWTAG));
	}
	
	@Test
	void testCreate_AlreadyExistsException() throws Exception {
		Tag tag = new Tag();
		tag.setName("Pasta");
		
		AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
			tagService.create(tag);
		});
		
		Assertions.assertEquals("Categorie Pasta bestaat al",exception.getMessage());
		assertEquals(4, tagService.getReadonlyTagList().size());
	}
	
	@Test
	void testCreate_IllegalValueException() throws Exception {
		Tag tag = new Tag();
		tag.setName("");
		
		IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
			tagService.create(tag);
		});
		
		Assertions.assertEquals("Categorie naam mag niet leeg zijn",exception.getMessage());
		assertEquals(4, tagService.getReadonlyTagList().size());
	}
	
	@Test 
	void testUpdate_HappyPath() throws Exception {
		Tag originalTag = tagService.findByName("Makkelijk").get();

		Tag expectedTag = testData.getTag(2L, "Moeilijk");
		when(tagRepository.save(expectedTag)).thenReturn(expectedTag);
		
		assertEquals(expectedTag, tagService.update(originalTag, "Moeilijk"));
		assertEquals(4, tagService.getReadonlyTagList().size());
		assertEquals(Optional.of(expectedTag), tagService.findById(2L));
	}
	
	@Test
	void testUpdate_NotFoundException() throws Exception {
		Tag originalTag = testData.getTag(3000L, "onbekend");
		
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
			tagService.update(originalTag, "bekend");
		});
		
		Assertions.assertEquals("Categorie onbekend niet gevonden",exception.getMessage());
		assertEquals(4, tagService.getReadonlyTagList().size());
	}
	
	@Test
	void testUpdate_AlreadyExistsException() throws Exception {
		Tag originalTag = testData.getTag(1L, "Vegetarisch");
		
		AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
			tagService.update(originalTag, "Feestelijk");
		});
		
		Assertions.assertEquals("Categorie Feestelijk bestaat al",exception.getMessage());
		assertEquals(4, tagService.getReadonlyTagList().size());
	}
	

	@Test 
	void testRemove_HappyPath() throws Exception {
		Tag originalTag = tagService.findByName("Makkelijk").get();

		assertEquals(4, tagService.getReadonlyTagList().size());
		tagService.remove(originalTag);
		assertEquals(3, tagService.getReadonlyTagList().size());
		assertEquals(Optional.empty(), tagService.findByName(originalTag.getName()));
	}
	
	@Test 
	void testRemove_NotFound() throws Exception {
		Tag originalTag = testData.getTag(3000L, "Onbekend");

		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
			tagService.remove(originalTag);
		});
		
		Assertions.assertEquals("Categorie Onbekend niet gevonden",exception.getMessage());
		assertEquals(4, tagService.getReadonlyTagList().size());
	}
}
