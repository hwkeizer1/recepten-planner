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

import nl.recipes.domain.IngredientName;
import nl.recipes.domain.IngredientType;
import nl.recipes.domain.MeasureUnit;
import nl.recipes.domain.ShopType;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.exceptions.IllegalValueException;
import nl.recipes.exceptions.NotFoundException;
import nl.recipes.repositories.IngredientNameRepository;
import nl.recipes.repositories.MeasureUnitRepository;
import nl.recipes.util.TestData;

class IngredientNameServiceTest {

	private static final String NEW_INGREDIENTNAME = "NewIngredientName";
	private static final String NEW_INGREDIENTNAMES = "NewIngredientNames";
	
	@Mock 
	IngredientNameRepository ingredientNameRepository;
	
	@InjectMocks 
	IngredientNameService ingredientNameService;
	
	TestData testData;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		testData = new TestData();
		ingredientNameService.setObservableIngredientNameList(testData.getIngredientNameList());
	}
	
	@Test
	void testGetReadonlyIngredientNameList() {
		List<IngredientName> expectedList = testData.getIngredientNameList();
		
		assertEquals(expectedList, ingredientNameService.getReadonlyIngredientNameList());
	}
	
	@Test
	void testFindByName_HappyPath() {
		Optional<IngredientName> expectedIngredientName = Optional.of(testData.getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));
		
		assertEquals(expectedIngredientName,  ingredientNameService.findByName("prei"));
	}
	
	@Test
	void testFindByName_NotFound() {
		Optional<IngredientName> expectedIngredientName = Optional.empty();
		
		assertEquals(expectedIngredientName,  ingredientNameService.findByName("prij"));
	}
	
	@Test
	void testFindById_HappyPath() {
		Optional<IngredientName> expectedIngredientName = Optional.of(testData.getIngredientName(3L, "prei", "preien", false, ShopType.DEKA, IngredientType.GROENTE));
		
		assertEquals(expectedIngredientName,  ingredientNameService.findById(3L));
	}
	
	@Test
	void testFindById_NotFound() {
		Optional<IngredientName> expectedIngredientName = Optional.empty();
		
		assertEquals(expectedIngredientName,  ingredientNameService.findById(3000L));
	}
	
	@Test 
	void testCreate_HappyPath() throws Exception {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setName(NEW_INGREDIENTNAME);
		ingredientName.setPluralName(NEW_INGREDIENTNAMES);
		ingredientName.setStock(true);
		ingredientName.setShopType(ShopType.MARKT);
		ingredientName.setIngredientType(IngredientType.OVERIG);
		IngredientName savedIngredientName = testData.getIngredientName(5L, NEW_INGREDIENTNAME, NEW_INGREDIENTNAMES, true, ShopType.MARKT, IngredientType.OVERIG);
		when(ingredientNameRepository.save(ingredientName)).thenReturn(savedIngredientName);
		
		assertEquals(savedIngredientName, ingredientNameService.create(ingredientName));
		assertEquals(5, ingredientNameService.getReadonlyIngredientNameList().size());
		assertEquals(Optional.of(savedIngredientName), ingredientNameService.findByName(NEW_INGREDIENTNAME));
	}
	
	@Test
	void testCreate_AlreadyExistsException() throws Exception {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setName("water");
		
		AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
			ingredientNameService.create(ingredientName);
		});
		
		Assertions.assertEquals("Ingrediënt naam water bestaat al",exception.getMessage());
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
	}
	
	@Test
	void testCreate_IllegalValueException() throws Exception {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setName("");
		
		IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, () -> {
			ingredientNameService.create(ingredientName);
		});
		
		Assertions.assertEquals("Ingrediënt naam mag niet leeg zijn",exception.getMessage());
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
	}
	
	@Test 
	void testUpdate_HappyPath() throws Exception {
		IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();
		IngredientName update = new IngredientName();
		update.setName("wortel");
		update.setPluralName("wortels");
		update.setStock(false);
		update.setShopType(ShopType.EKO);
		update.setIngredientType(IngredientType.GROENTE);

		IngredientName expectedIngredientName = testData.getIngredientName(2L, "wortel", "wortels", false, ShopType.EKO, IngredientType.GROENTE);
		when(ingredientNameRepository.save(expectedIngredientName)).thenReturn(expectedIngredientName);
		
		assertEquals(expectedIngredientName, ingredientNameService.update(originalIngredientName, update));
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
		assertEquals(Optional.of(expectedIngredientName), ingredientNameService.findById(2L));
	}
	
	@Test
	void testUpdate_NotFoundException() throws Exception {
		IngredientName originalIngredientName = testData.getIngredientName(3000L, "onbekend", "onbekenden", true, ShopType.DEKA, IngredientType.GROENTE);
		IngredientName update = new IngredientName();
		update.setName("bekend");
		update.setPluralName("bekenden");
		
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
			ingredientNameService.update(originalIngredientName, update);
		});
		
		Assertions.assertEquals("Ingrediënt naam onbekend niet gevonden",exception.getMessage());
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
	}
	
	@Test
	void testUpdate_AlreadyExistsException() throws Exception {
		IngredientName originalIngredientName = testData.getIngredientName(1L, "water", "water", true, null, IngredientType.OVERIG);
		IngredientName update = new IngredientName();
		update.setName("mozzarella");
		update.setPluralName("mozzarella");
		
		AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
			ingredientNameService.update(originalIngredientName, update);
		});
		
		Assertions.assertEquals("Ingrediënt naam mozzarella bestaat al",exception.getMessage());
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
	}
	
	@Test 
	void testRemove_HappyPath() throws Exception {
		IngredientName originalIngredientName = ingredientNameService.findByName("ui").get();

		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
		ingredientNameService.remove(originalIngredientName);
		assertEquals(3, ingredientNameService.getReadonlyIngredientNameList().size());
		assertEquals(Optional.empty(), ingredientNameService.findByName(originalIngredientName.getName()));
	}

	@Test 
	void testRemove_NotFound() throws Exception {
		IngredientName originalIngredientName = testData.getIngredientName(3000L, "onbekend", "onbekenden", true, null, IngredientType.OVERIG);

		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
			ingredientNameService.remove(originalIngredientName);
		});
		
		Assertions.assertEquals("Ingrediënt naam onbekend niet gevonden",exception.getMessage());
		assertEquals(4, ingredientNameService.getReadonlyIngredientNameList().size());
	}

}
