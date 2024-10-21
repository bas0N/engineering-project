import translate from 'google-translate-api';
import fs from 'fs';

const translationFilesPaths = ['../../Main/public/locales/','../../auth-module/public/locales/'];

const watchPaths = translationFilesPaths.map((path) => `${path}translation.en.json`);

//translate.engine = 'deepl';
//translate.key = 'DEEPL_KEY';

const getNestedPropertyFromPath = (obj, path) => {
    const keys = path.split('.');
    let current = obj;
    
    for (let i = 0; i < keys.length; i++) {
        if (!current[keys[i]]) {
            return;
        }
        current = current[keys[i]];
    }

    return current;
}

const setNestedPropertyFromPath = (obj, path, value) => {
    const keys = path.split('.');
    let current = obj;
    
    for (let i = 0; i < keys.length - 1; i++) {
        if (!current[keys[i]]) {
            current[keys[i]] = {}; // Create the nested object if it doesn't exist
        }
        current = current[keys[i]];
    }

    current[keys[keys.length - 1]] = value;
}

const languages = Object.keys(translate.languages).filter((lang) => !lang.includes('-') && lang !== 'auto' && lang !== 'isSupported' && lang !== 'getCode'); 
const generateAllTranslationPaths = (translation) => {
    const translationsKeys = [];
    const finalKeys = [];

    translationsKeys.push([...Object.keys(translation)]);

    while(translationsKeys.length > 0){
        const firstKey = translationsKeys.shift(); // shift returns an array, whilst we need the string at the first position of that new array
        const currentKey = firstKey[0];
        const keyProperty = getNestedPropertyFromPath(translation, currentKey);
        if(typeof firstKey === 'object'){
            const keyPropertySubKeys = Object.keys(keyProperty);
            keyPropertySubKeys.forEach((subKey) => {
                translationsKeys.push(`${currentKey}.${subKey}`);
            });
        } else {
            finalKeys.push(firstKey);
        }
    }

    return finalKeys;
}

const translateToNewLanguage = async(translation, lang, translationPaths) => {
    // Here add the translation to the language with the use of translationPaths
    const newTranslation = {...translation};

    translationPaths.forEach(async (path) => {
        const value = getNestedPropertyFromPath(translation, path);
        try {
            const text = await translate(value, {to: lang});
            setNestedPropertyFromPath(newTranslation, path, text);
        } catch (err) {
            console.log(`Error while processing the translation to ${lang}: ${err}`, err);
        }
    });

    return newTranslation;

}

const readMainTranslationFile = async (filePath, savingPath) => {
    fs.readFile(filePath, 'utf8', (err, data) => {
        if (err) {
            console.error(`Could not read the ${filePath} file with the following error:`, err);
            return;
        }
        try {
            const translation = JSON.parse(data);
            const translationPaths = generateAllTranslationPaths(translation);
            languages.forEach(async (lang) => {
                if (lang !== 'en') {
                    const newTranslation = await translateToNewLanguage(translation, lang, translationPaths);
                    console.log(newTranslation);
                    fs.writeFile(`${savingPath}translation.${lang}.json`, JSON.stringify(newTranslation, null, 2), 'utf-8', (err) => {
                        if (err) {
                            console.error(`Error writing to the translation.${lang}.json file:`, err);
                            return;
                        }
                        console.log(`File successfully updated the translation.${lang}.json file`);
                    });
                }
            });

        } catch (parseError) {
            console.error(`Error parsing JSON data from ${filePath}:`, parseError);
        }
    });
};

watchPaths.forEach((watchPath, ind) => readMainTranslationFile(watchPath, translationFilesPaths[ind]));