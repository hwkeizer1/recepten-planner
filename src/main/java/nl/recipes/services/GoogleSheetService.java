package nl.recipes.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CopyPasteRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import nl.recipes.domain.Ingredient;
import nl.recipes.domain.ShoppingItem;

@Slf4j
@Service
public class GoogleSheetService {

  private static final String APPLICATION_NAME = "Recipe planner";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  // TODO: make this configurable
  final String spreadsheetId = "1xjI9CqMGhfebUDOFEezvcVYlLnW7gAGj4THiskdtkQ0";
  final Integer shoppingSheetId = 2025323414;
  final Integer templateSheetId = 1929719991;

  private Sheets sheetService;

  private int startRow;

  public boolean credentialsValid() {
      NetHttpTransport httpTransport;
      try {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return validateCredentials(httpTransport);
      } catch (GeneralSecurityException | IOException e) {
        log.error("Fout tijdens benaderen Google sheets");
        e.printStackTrace();
        return false;
      }
  }
  
  private boolean validateCredentials(NetHttpTransport httpTransport) throws IOException {
    InputStream in = GoogleSheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline").build();
    Credential credential = flow.loadCredential("user");
    return (credential != null
        && (credential.getRefreshToken() != null
            || credential.getExpiresInSeconds() == null
            || credential.getExpiresInSeconds() > 60)); 
  }

  public Sheets createSheetService() {
    NetHttpTransport HTTP_TRANSPORT;
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      sheetService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
          .setApplicationName(APPLICATION_NAME).build();
      return sheetService;
    } catch (GeneralSecurityException | IOException e) {
      log.error("Fout tijdens benaderen Google sheets");
      e.printStackTrace();
      return null;
    }
  }

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    InputStream in = GoogleSheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline").build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  // Keep for now to check if it will be needed when credentials are revoked
  private boolean deleteStoredCredentials() {
    Path rootPath = Path.of("").toAbsolutePath();
    Path storedCredentialsPath = Path.of(rootPath.toString(), TOKENS_DIRECTORY_PATH, "StoredCredential");
    File storedCredentials = storedCredentialsPath.toFile();
    try {
      Files.deleteIfExists(storedCredentialsPath);
    } catch (IOException e) {
      log.debug("Fout bij het verwijderen van de bewaarde credentials");
    }
    return !storedCredentials.exists();
  }

  public void setEkoShoppings(List<Ingredient> ekoIngredientList, List<ShoppingItem> ekoShoppingList) {
    startRow = 0;

    int totalEkoItems = ekoIngredientList.size() + ekoShoppingList.size();
    try {
      int lastRow = prepareSpreadsheet(spreadsheetId, totalEkoItems, startRow, 0);

      List<List<Object>> items = new ArrayList<>();
      for (Ingredient ingredient : ekoIngredientList) {
        items
            .add(Arrays.asList((ingredient.getAmount() == null) ? "" : ingredient.getAmount().toString(),
                (ingredient.getIngredientName().getMeasureUnit() == null) ? ""
                    : ingredient.getIngredientName().getMeasureUnit().getName(),
                ingredient.getIngredientName().getName()));
      }

      for (ShoppingItem shoppingItem : ekoShoppingList) {
        items.add(Arrays.asList("", "", shoppingItem.getIngredientName().getName()));
      }

      ValueRange body = new ValueRange().setValues(items);
      getSheetService().spreadsheets().values().update(spreadsheetId, "A" + (startRow + 2), body)
          .setValueInputOption("USER_ENTERED").execute();
      this.startRow = lastRow;
    } catch (IOException e) {
      log.error("Fout tijdens benaderen Google sheets");
      e.printStackTrace();
    }
  }

  public void setDekaShoppings(List<Ingredient> dekaIngredientList, List<ShoppingItem> dekaShoppingList) {
    int totalDekaItems = dekaIngredientList.size() + dekaShoppingList.size();
    try {
      int lastRow = prepareSpreadsheet(spreadsheetId, totalDekaItems, startRow, 1);

      List<List<Object>> items = new ArrayList<>();
      for (Ingredient ingredient : dekaIngredientList) {
        items
            .add(Arrays.asList((ingredient.getAmount() == null) ? "" : ingredient.getAmount().toString(),
                (ingredient.getIngredientName().getMeasureUnit() == null) ? ""
                    : ingredient.getIngredientName().getMeasureUnit().getName(),
                ingredient.getIngredientName().getName()));
      }

      for (ShoppingItem shoppingItem : dekaShoppingList) {
        items.add(Arrays.asList("", "", shoppingItem.getIngredientName().getName()));
      }

      ValueRange body = new ValueRange().setValues(items);
      getSheetService().spreadsheets().values().update(spreadsheetId, "A" + (startRow + 2), body)
          .setValueInputOption("USER_ENTERED").execute();
      this.startRow = lastRow;
    } catch (IOException e) {
      log.error("Fout tijdens benaderen Google sheets");
      e.printStackTrace();
    }
  }

  public void setMarktShoppings(List<Ingredient> marktIngredientList, List<ShoppingItem> marktShoppingList) {
    int totalMarktItems = marktIngredientList.size() + marktShoppingList.size();
    try {
      int lastRow = prepareSpreadsheet(spreadsheetId, totalMarktItems, startRow, 2);

      List<List<Object>> items = new ArrayList<>();
      for (Ingredient ingredient : marktIngredientList) {
        items
            .add(Arrays.asList((ingredient.getAmount() == null) ? "" : ingredient.getAmount().toString(),
                (ingredient.getIngredientName().getMeasureUnit() == null) ? ""
                    : ingredient.getIngredientName().getMeasureUnit().getName(),
                ingredient.getIngredientName().getName()));
      }

      for (ShoppingItem shoppingItem : marktShoppingList) {
        items.add(Arrays.asList("", "", shoppingItem.getIngredientName().getName()));
      }

      ValueRange body = new ValueRange().setValues(items);
      getSheetService().spreadsheets().values().update(spreadsheetId, "A" + (startRow + 2), body)
          .setValueInputOption("USER_ENTERED").execute();
      this.startRow = lastRow;
    } catch (IOException e) {
      log.error("Fout tijdens benaderen Google sheets");
      e.printStackTrace();
    }
  }

  public void setOtherShoppings(List<Ingredient> otherIngredientList, List<ShoppingItem> otherShoppingList) {
    int totalOtherItems = otherIngredientList.size() + otherShoppingList.size();
    try {
      int lastRow = prepareSpreadsheet(spreadsheetId, totalOtherItems, startRow, 3);

      List<List<Object>> items = new ArrayList<>();
      for (Ingredient ingredient : otherIngredientList) {
        items
            .add(Arrays.asList((ingredient.getAmount() == null) ? "" : ingredient.getAmount().toString(),
                (ingredient.getIngredientName().getMeasureUnit() == null) ? ""
                    : ingredient.getIngredientName().getMeasureUnit().getName(),
                ingredient.getIngredientName().getName()));
      }

      for (ShoppingItem shoppingItem : otherShoppingList) {
        items.add(Arrays.asList("", "", shoppingItem.getIngredientName().getName()));
      }

      ValueRange body = new ValueRange().setValues(items);
      getSheetService().spreadsheets().values().update(spreadsheetId, "A" + (startRow + 2), body)
          .setValueInputOption("USER_ENTERED").execute();
      this.startRow = lastRow;

      // This is the last shoppinglist part, clear possible data below this part
      clearSheetBelowShoppingList();

    } catch (IOException e) {
      log.error("Fout tijdens benaderen Google sheets");
      e.printStackTrace();
    }
  }

  private Sheets getSheetService() {
    return (sheetService == null) ? createSheetService() : sheetService;
  }
  
  private void clearSheetBelowShoppingList() throws IOException {
    List<Request> requests = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      requests.add(requestEmptyRow(startRow + i));
    }

    BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
    content.setRequests(requests);
    getSheetService().spreadsheets().batchUpdate(spreadsheetId, content).execute();
  }

  private int prepareSpreadsheet(String spreadsheetId, int listSize, int startRow, int headerIndex) throws IOException {
    List<Request> requests = new ArrayList<>();

    BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();

    int rowIndex = startRow;
    Request headerCopyPasteRequest = new Request().setCopyPaste(new CopyPasteRequest()
        .setSource(new GridRange().setSheetId(templateSheetId).setStartRowIndex(headerIndex)
            .setEndRowIndex(headerIndex + 1).setStartColumnIndex(0).setEndColumnIndex(4))
        .setDestination(new GridRange().setSheetId(shoppingSheetId).setStartRowIndex(rowIndex)
            .setEndRowIndex(++rowIndex).setStartColumnIndex(0).setEndColumnIndex(4))
        .setPasteType("PASTE_NORMAL").setPasteOrientation("NORMAL"));
    requests.add(headerCopyPasteRequest);
    for (int i = 0; i < listSize; i++) {
      requests.add(requestRowWithSelection(rowIndex));
      rowIndex++;
    }

    content.setRequests(requests);
    getSheetService().spreadsheets().batchUpdate(spreadsheetId, content).execute();
    return rowIndex;
  }

  private Request requestRowWithSelection(int rowIndex) {
    return new Request().setCopyPaste(new CopyPasteRequest()
        .setSource(new GridRange().setSheetId(templateSheetId).setStartRowIndex(4).setEndRowIndex(5)
            .setStartColumnIndex(0).setEndColumnIndex(4))
        .setDestination(new GridRange().setSheetId(shoppingSheetId).setStartRowIndex(rowIndex)
            .setEndRowIndex(rowIndex + 1).setStartColumnIndex(0).setEndColumnIndex(4))
        .setPasteType("PASTE_NORMAL").setPasteOrientation("NORMAL"));
  }

  private Request requestEmptyRow(int rowIndex) {
    return new Request().setCopyPaste(new CopyPasteRequest()
        .setSource(new GridRange().setSheetId(templateSheetId).setStartRowIndex(5).setEndRowIndex(6)
            .setStartColumnIndex(0).setEndColumnIndex(4))
        .setDestination(new GridRange().setSheetId(shoppingSheetId).setStartRowIndex(rowIndex)
            .setEndRowIndex(rowIndex + 1).setStartColumnIndex(0).setEndColumnIndex(4))
        .setPasteType("PASTE_NORMAL").setPasteOrientation("NORMAL"));
  }
}
