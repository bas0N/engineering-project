import { 
    InteractionTag, 
    InteractionTagPrimary, 
    InteractionTagSecondary,
    Text
} from "@fluentui/react-components";
import { ChangeEvent, KeyboardEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { CategoriesWrapper, CategoriesInput, CategoriesTagsWrapper } from './Categories.styled';

interface CategoriesProps {
    categories: string[];
    setCategories: (categories: string[]) => void;
}

export const Categories = ({
    categories,
    setCategories
} : CategoriesProps) => {
    const {t} = useTranslation();
    const [newCategory, setNewCategory] = useState('');

    const handleAddingNewCategory = () => {
        if(!categories.find((searchedCategory) => searchedCategory === newCategory)) {
            const operand = [...categories, newCategory];
            setCategories(operand)
            setNewCategory('');
        }
    }

    const removeCategory = (category: string) => {
        const operand = categories.filter((elem) => elem !== category);
        setCategories(operand);
    }

    return (<CategoriesWrapper>
        <Text as='h3' align='center'>{t('addProduct.categories.header')}</Text>
        <CategoriesInput 
            appearance='underline'
            value={newCategory}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setNewCategory(e.currentTarget.value)}
            onKeyDown={(e: KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleAddingNewCategory()}
            placeholder={t('addProduct.categories.newCategoryPlaceholder')}
        />
        <CategoriesTagsWrapper>
            {
                categories.map((category) => <InteractionTag value={category} key={category}>
                <InteractionTagPrimary
                    hasSecondaryAction
                >
                    {category}
                </InteractionTagPrimary>
                <InteractionTagSecondary 
                    aria-label="remove" 
                    onClick={() => removeCategory(category)}
                    />
                </InteractionTag>)
            }
        </CategoriesTagsWrapper>
    </CategoriesWrapper>)
};