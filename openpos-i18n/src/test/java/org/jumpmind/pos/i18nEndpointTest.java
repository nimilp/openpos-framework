package org.jumpmind.pos;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jumpmind.pos.i18n.service.i18nEndpoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class i18nEndpointTest {
	
//	@Test
//	public void performance () {
//		String base = "test";
//		String key = "_company1";
//		Locale locale = Locale.FRANCE;
//		String brand = "BigLots";
//		String[] args = {"builder"};
//		long tic, toc, time;
//		
//		tic = System.nanoTime();
//		for (int i = 0; i < 500000; i++) {
//			i18nEndpoint.getString(base, key, locale, brand);
//		}
//		toc = System.nanoTime();
//		time = toc - tic;
//		System.out.println("Concat completed in: \t" + time);	
//	}
	
	i18nEndpoint i18nEndpoint;
	
	@Before
	public void setup () {
		i18nEndpoint = new i18nEndpoint();
	}
	
	@Test
	/**
	 * Test missing resource with incorrect base with a brand field
	 */
	public void getStringMissingResourceBaseTest () {
		String base = "nothing";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	@Test
	/**
	 * Test missing resource with incorrect base with no brand field
	 */
	public void getStringMissingResourceBaseNoBrandTest () {
		String base = "nothing";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	@Test
	/**
	 *  Test missing resource with a properties file that exists, but bad key
	 */
	public void getStringMissingResourceKeyTest () {
		String base = "test";
		String key = "_nothing";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	@Test
	/**
	 *  Test missing resource with a properties file and brand that exists, but bad key
	 */
	public void getStringMissingResourceKeyNoBrandTest () {
		String base = "test";
		String key = "_nothing";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	@Test
	/**
	 *  Test for an unrecognized locale to default to the default brand file
	 */
	public void getStringIncorrectToDefaultLocaleTest () {
		String base = "test";
		String key = "_company";
		Locale locale = new Locale("foo", "foh");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<Company Name>");
	}
	
	@Test
	/**
	 *  Test for an unrecognized locale to default to the default locale file
	 */
	public void getStringIncorrectToDefaultLocaleNoBrandTest () {
		String base = "test";
		String key = "_me";
		Locale locale = new Locale("foo", "foh");
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "me");
	}
	
	@Test
	/**
	 *  Test for a bad brand with no key in any test_other file
	 */
	public void getStringMissingResourceBrandTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.FRANCE;
		String brand = "4Tran";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	@Test
	/**
	 *  Test for a bad brand with antest_other valid key in antest_other file
	 */
	public void getStringBadBrandUseLocaleTest () {
		String base = "test";
		String key = "_company";
		Locale locale = Locale.FRANCE;
		String brand = "4Tran";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<Enterprise Titre>");
	}
	
	@Test
	/**
	 *  Basic Translation from locale file Test
	 */
	public void getStringTranslateNoBrandTest () {
		String base = "test";
		String key = "_company";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<Enterprise Titre>");
	}
	
	@Test
	/**
	 *  Basic locale override test
	 */
	public void getStringTranslateOverrideNoBrandTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<Las Enterprise>");
	}
	
	@Test
	/**
	 *  Test to get from brand file from default locale
	 */
	public void getStringBrandDefaultTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Big Lots");
	}
	
	@Test
	/**
	 *  Test to ensure the brand override file works
	 */
	public void getStringBrandDefaultOverrideTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Biggest Lots");
	}
	
	@Test
	/**
	 *  Test to translate from brand file
	 */
	public void getStringTranslateBrandTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Grand Lots");
	}
	
	@Test
	/**
	 *  Test to override translation from brand file
	 */
	public void getStringTranslateBrandOverrideTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Plus Grand Lots");
	}
	
	@Test
	/**
	 * 	Test to ensure retrieving english strings works
	 */
	public void getStringDefaultNoBrandTest () {
		String base = "test";
		String key = "_you";
		Locale locale = Locale.getDefault();
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "you");
	}
	
	@Test
	/**
	 *  Test to ensure brand does not interfere with getting english strings
	 */
	public void getStringDefaultBrandNoConseqTest () {
		String base = "test";
		String key = "_you";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "you");
	}
	
	
	@Test
	/**
	 * Test to ensure brand does not interfere with translation
	 */
	public void getStringTranslateBrandNoConseqTest () {
		String base = "test";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "moi");
	}
	
	@Test
	/**
	 *  Test where (translation) key is in the override file but not the base locale file
	 */
	public void getStringTranslateInOverrideNotBaseTest () {
		String base = "test";
		String key = "_override";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Je m'appelle");
	}
	
	@Test
	/**
	 *  Test where (english) key is in the override file but not the base locale file
	 */
	public void getStringDefaultInOverrideNotBaseTest () {
		String base = "test";
		String key = "_override";
		Locale locale = Locale.getDefault();
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "My name is");
	}
	
	@Test
	/**
	 * Test to get (english) key in the brand override file, not the base brand file
	 */
	public void getStringDefaultInBrandOverrideNotBrandTest () {
		String base = "test";
		String key = "_overrideB";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Biggest Lots Override");
	}
	
	@Test
	/**
	 * Test to get (translation) key in the brand override file, not the base brand file
	 */
	public void getStringTranslateInBrandOverrideNotBrandTest () {
		String base = "test";
		String key = "_overrideB";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Plus Grand Lots Override");
	}
	
	@Test
	/**
	 * Test to get (translation) key in the brand file, not the base locale file
	 */
	public void getStringTranslateInBrandNotBaseTest () {
		String base = "test";
		String key = "_logo_color";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "jaune");
	}
	
	@Test
	/**
	 * Test where locale key field is blank
	 */
	public void getStringTranslateEmptyField () {
		String base = "test";
		String key = "_empty";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "");
	}
	
	@Test
	/**
	 * Test where brand override key field is blank, but brand is not
	 */
	public void getStringTranslateEmptyFieldInOverride () {
		String base = "test";
		String key = "_logo_color1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "");
	}
	
	@Test
	/**
	 * Test to translate a key in a base, not "test"
	 */
	public void getStringTranslateDifferentBaseTest () {
		String base = "test_other";
		String key = "_are";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "�tes");
	}
	
	@Test
	/**
	 * Test to translate a key in a non-French, non-constant Locale with no brand
	 */
	public void getStringTranslateNonConstantLocaleNoBrandTest () {
		String base = "test";
		String key = "_hello";
		Locale locale = new Locale ("es", "MX");
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Hola!");
	}
	
	@Test
	/**
	 * Test to translate a key in a non-French, non-constant Locale with a Brand
	 */
	public void getStringTranslateNonConstantLocale () {
		String base = "test";
		String key = "_hello";
		Locale locale = new Locale ("es", "MX");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "Hola!");
	}
	
	@Test
	/**
	 * Test where key is bad, but good in different locale
	 */
	public void getStringKeyInDifferentLocale () {
		String base = "test";
		String key = "_you";
		Locale locale = new Locale ("es", "MX");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale, brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	
	/* 	Message Formatter Tests */
	
	@Test
	/**
	 *  Test for date and time format with NO format specifier
	 */
	public void getStringMessageFormatDateTimeTest () {
		String base = "test";
		String key = "_test_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "At " + DateFormat.getTimeInstance().format(date) + 
				" on " + DateFormat.getDateInstance().format(date);

		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for date and time format with SHORT format specifier
	 */
	public void getStringMessageFormatDateTimeShortTest () {
		String base = "test";
		String key = "_test_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "At " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date) + 
				" on " + DateFormat.getDateInstance(DateFormat.SHORT).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for TRANSLATION date and time format with NO format specifier
	 */
	public void getStringMessageTranslateFormatDateTimeTest () {
		String base = "test";
		String key = "_test_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "� " + DateFormat.getTimeInstance(DateFormat.DEFAULT, locale).format(date) + 
				" en " + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for TRANSLATION date and time format with SHORT format specifier
	 */
	public void getStringMessageTranslateFormatDateTimeShortTest () {
		String base = "test";
		String key = "_test_string1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "� " + DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date) + 
				" en " + DateFormat.getDateInstance(DateFormat.SHORT, locale).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for currency format US Eng
	 */
	public void getStringMessageFormatCurrencyTest () {
		String base = "test";
		String key = "_currency_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				1,
		};

		String exp = "I have $1.00"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for currency format French
	 */
	public void getStringMessageTranslateFormatCurrencyTest () {
		String base = "test";
		String key = "_currency_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				1,
		};

		String exp = "J'ai 1,00 �"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for number format in US Eng
	 */
	public void getStringMessageFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "It's the number 34,592.09"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	//TODO Report the weird French separator thing
	
	@Test
	/**
	 *  Test for currency format in French
	 */
	public void getStringMessageTranslateFrFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "C'est le nombre 34\u00a0592,09"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for number format in German
	 */
	public void getStringMessageTranslateDeFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.GERMANY;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "Es ist die Nummer 34.592,09"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for number (INTEGER) format US Eng
	 */
	public void getStringMessageFormatNumberIntegerTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "It's the number 34,592"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for number (INTEGER) format US French
	 */
	public void getStringMessageTranslateFormatNumberIntegerTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "C'est le nombre 34\u00a0592"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for conflicting parameter passed
	 */
	public void getStringMessageBadParameterNumberTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				"DC makes a good movie",
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for no parameters passed
	 */
	public void getStringMessageNoParameterNumberTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				
		};

		String exp = "It's the number {0}"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for pattern with invalid format passed with no args
	 */
	public void getStringMessageBadPatternNoArgsTest () {
		String base = "test";
		String key = "_bad_pattern0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				,
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for pattern with invalid format passed with no args
	 */
	public void getStringMessageBadPatternWithArgsTest () {
		String base = "test";
		String key = "_bad_pattern0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				"small cats",
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for pattern with invalid (not a number) parameter reference
	 */
	public void getStringMessageBadPatternBadReferenceTest () {
		String base = "test";
		String key = "_bad_pattern1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for pattern with custom style string
	 */
	public void getStringMessageBadPatternWrongFormatSpecTest () {
		String base = "test";
		String key = "_custom_style0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "Nous avons \"custom_style\"45"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
	
	@Test
	/**
	 *  Test for pattern with quoted brackets
	 */
	public void getStringMessageQuotedTest () {
		String base = "test";
		String key = "_quoted";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "Nous avons {0, number}"; 
		
		String res = i18nEndpoint.getString(base, key, locale, brand, args);
		Assert.assertEquals(res, exp);
	}
		
}
