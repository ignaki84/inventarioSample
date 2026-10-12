package com.idm.demos.inventario.utilidades;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idm.demos.inventario.model.ActivoTecnologicoView;

public class FileCreator {
	
	private final String CSV_FILE = "registros.csv";
	private final String TXT_FILE = "auditoria.txt";
	
	public String zipAuditFile(List<ActivoTecnologicoView> activos, String user) 
			throws RuntimeException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try ( ZipOutputStream zos = new ZipOutputStream(baos)) {
			
			//archivo de excel
			ZipEntry csvEntry = new ZipEntry(CSV_FILE);
            zos.putNextEntry(csvEntry);
            zos.write(csvData(activos).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            
            //archivo de auditoria
            ZipEntry txtEntry = new ZipEntry(TXT_FILE);
            zos.putNextEntry(txtEntry);
            zos.write(auditData(user,activos.size()).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // Finalizar la compresión del ZIP
            zos.finish();
		
			
		} catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo ZIP de auditoría: " + e.getMessage(), e);
        }
		
		String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
		
		return base64;
	}
	
	private String csvData(List<ActivoTecnologicoView> activos) {
		StringBuilder csv = new StringBuilder();
        csv.append("ID Producto,Folio,Serie,Marca,Costo,Estado,Categoria,Fecha Creacion\n");
        for (ActivoTecnologicoView activo : activos) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    activo.getIdProducto(), 
                    activo.getFolio(), 
                    activo.getSerie(),
                    activo.getMarca(), 
                    activo.getCosto(), 
                    activo.getEstado(),
                    activo.getCategoria(), 
                    activo.getCreacion()));
        }
        
        return csv.toString();
	}
	
	private String auditData(String user, Integer rows) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String txtAuditoria = String.format("Fecha y hora de generación: %s\nUsuario solicitante: %s\nTotal de registros exportados: %d\n",
                LocalDateTime.now().format(formatter),
                user,
                rows);
        return txtAuditoria;
	}

}
