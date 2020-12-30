package org.startupkit.user.helper;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.startupkit.user.User;
import org.startupkit.user.UserDAO;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserHelperServiceImpl implements UserHelperService {
	
	
	
	@Inject
	@New
	private UserDAO userDAO;
	
	

	@Override
	public byte[] excelUsersDatabase() throws Exception {
		
		byte[] data = null;
		
		List<User> list = userDAO.listAll();
		
		HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("usuarios");
 
        int rowCount = 0;
        
        Row row = sheet.createRow(rowCount++);
        
        int columnCount = 0;
         
        Cell cellName = row.createCell(columnCount++);
        cellName.setCellValue("Nome");
        
        Cell cellEmail = row.createCell(columnCount++);
        cellEmail.setCellValue("Email");
            
         
        for (User user : list) {
            row = sheet.createRow(rowCount++);
             
            columnCount = 0;
             
            cellName = row.createCell(columnCount++);
            cellName.setCellValue(user.getName());
            
            cellEmail = row.createCell(columnCount++);
            cellEmail.setCellValue(user.getEmail());
        }
         
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            data = outputStream.toByteArray();
        }
		
		return data;
	}
}
