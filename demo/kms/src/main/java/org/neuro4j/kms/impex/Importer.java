package org.neuro4j.kms.impex;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.neuro4j.core.Network;
import org.neuro4j.core.Representation;
import org.neuro4j.kms.Config;
import org.neuro4j.kms.Utils;
import org.neuro4j.kms.d.ExternalExperience;
import org.neuro4j.kms.d.Word;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

public class Importer {


	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Storage storage = null;
	
	public Importer(Storage storage)
	{
		this.storage = storage;		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		Importer importer = new Importer(Config.storage);

		importer.query("delete () ");
		
		importer.query("select () limit 3");
		
		importer.initialImport();
		
		importer.query("select () ");
	}

	public void initialImport()
	{
		Network net = new Network();
		
		ExternalExperience dog = new ExternalExperience();
		net.add(dog);
		// post dog Roger's photo
		Representation r1 = uploadRepresentation("./data/demo-files/dog_roger.jpg");
		dog.addRepresentation(r1);

		Utils.connect(net, dog, new Word("Dog", Config.EN));
		Utils.connect(net, dog, new Word("Пес", Config.RU));
		Utils.connect(net, dog, new Word("Кобель", Config.RU));
		Utils.connect(net, dog, new Word("Псина", Config.RU));
		Utils.connect(net, dog, new Word("Hund", Config.DE));
		Utils.connect(net, dog, new Word("Perro", Config.ES));
		
		Word dogLabelRu = new Word("Собака", Config.RU);
		Utils.connect(net, dog, dogLabelRu);
		
		ExternalExperience atImg = new ExternalExperience();
		atImg.addRepresentation(uploadRepresentation("./data/demo-files/at.jpg"));
//		atImg.setProperty("text", "@");
		atImg.setProperty("image", "@");
		
//		Utils.connect(net, atImg, new Word("@", Config.EN));
		Utils.connect(net, atImg, new Word("At/@", Config.EN));
		Utils.connect(net, atImg, dogLabelRu);
		
		ExternalExperience dogPooch = new ExternalExperience();
		Utils.connect(net, dogPooch, new Word("Pooch", Config.EN));
		Utils.connect(net, dogPooch, new Word("Mongrel", Config.EN));
		Utils.connect(net, dogPooch, new Word("Дворняжка", Config.RU));
		Utils.connect(net, dogPooch, dogLabelRu);

		ExternalExperience coin = new ExternalExperience();
		// add image
		Representation rubImg = uploadRepresentation("./data/demo-files/1-ruble.jpg");
		Representation dollarImg = uploadRepresentation("./data/demo-files/1-dollar.jpg");
		coin.addRepresentation(rubImg);
		coin.addRepresentation(dollarImg);
		
		Word wEnCoin = new Word("Coin", Config.EN);
		Utils.connect(net, coin, wEnCoin);
		Utils.connect(net, coin, new Word("Piece", Config.EN));
		Utils.connect(net, coin, new Word("Монета", Config.RU));
		Utils.connect(net, coin, new Word("Монетка", Config.RU));

		ExternalExperience ruble = new ExternalExperience();
		// add image
		ruble.addRepresentation(rubImg);
		Utils.connect(net, ruble, new Word("Ruble", Config.EN));
		Utils.connect(net, ruble, new Word("Rouble", Config.EN));
		Utils.connect(net, ruble, new Word("Рубль", Config.RU));

		ExternalExperience dollar = new ExternalExperience();
		// add image
		dollar.addRepresentation(dollarImg);
		Utils.connect(net, dollar, new Word("Dollar", Config.EN));
		Utils.connect(net, dollar, new Word("Доллар", Config.RU));
		

		ExternalExperience money = new ExternalExperience();
		Utils.connect(net, money, wEnCoin);
		Utils.connect(net, money, new Word("Money", Config.EN));
		Utils.connect(net, money, new Word("Деньги", Config.RU));
		
		try {
			storage.save(net);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public Network query(String query) 
	{
		try {
			Network net = storage.query(query);
			
			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
			return net;
		} catch (NQLException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
		return null;
	}	
	
	
	/**
	 * Read a file and upload it to storage as representation
	 * 
	 * @param fileName
	 * @return
	 */
	public Representation uploadRepresentation(String fileName)
	{
		Representation representation = new Representation();
		try {
			byte[] fileBytes = getFileData(fileName);
			if (null == fileBytes)
				logger.severe("Can't read " + fileName);
			
			representation.setData(storage, fileBytes);
			
			return representation;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

    private byte[] getFileData(String fileName) throws Exception {
        byte[] data = null;
        
        File file = new File(fileName);
        data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();

        return data;
    }	
}
