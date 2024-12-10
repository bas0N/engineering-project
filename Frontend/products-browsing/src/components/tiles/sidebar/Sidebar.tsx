import { ChangeEvent, useState } from 'react';
import { useTranslation } from "react-i18next";
import { DismissCircle48Regular } from '@fluentui/react-icons';
import { 
    SidebarWrapper, 
    SidebarHeader,
    SidebarCategoryWrapper,
    SidebarCategoryHeader,
    SidebarPriceInput,
    SidebarPriceContainer,
    SidebarCategoryDropdown,
    SidebarCategoryDropdownWrapper,
    SidebarClosingButton,
} from "./Sidebar.styled";
import { DropdownProps, Option, Text } from "@fluentui/react-components";

const SidebarFilterSection = ({header, children}:{header: string, children: JSX.Element[] | JSX.Element}) => (
    <SidebarCategoryWrapper>
        <SidebarCategoryHeader>
            {header}
        </SidebarCategoryHeader>
        {children}
    </SidebarCategoryWrapper>
);

interface SidebarProps {
    isOpened: boolean;
    closeSidebar: () => void;
    categories: string[];
    onCategoriesSelect: (categories: string[]) => void;
    minPrice: number|undefined;
    maxPrice: number|undefined;
    selectMinPrice: (price: number) => void;
    selectMaxPrice: (price: number) => void;
}

export const Sidebar = ({
    isOpened,
    closeSidebar,
    categories,
    onCategoriesSelect,
    minPrice,
    selectMinPrice,
    maxPrice,
    selectMaxPrice,
} : SidebarProps) => {
    const {t} = useTranslation();

    const [selectedCategories, setSelectedCategories] = useState<string[]>([]);

    const onCategorySelect: DropdownProps["onOptionSelect"] = (_ev, data) => {
        setSelectedCategories(data.selectedOptions);
        onCategoriesSelect(data.selectedOptions);
    };
    
    const processPriceChange = (newPrice: number, priceType: 'min' | 'max') => {
        if(
            (priceType === 'min' && maxPrice !== undefined && newPrice > maxPrice)
            || (priceType === 'max' && minPrice !== undefined && newPrice < minPrice)
        ) {
            return;
        }
        
        if(priceType === 'min') {
            selectMinPrice(newPrice);
        }
        else {
            selectMaxPrice(newPrice);
        }
    }
    
    return (
        <SidebarWrapper isOpened={isOpened}>
            <SidebarClosingButton appearance='transparent' onClick={closeSidebar}>
                <DismissCircle48Regular />
            </SidebarClosingButton>
            <SidebarHeader as='h2' align="center" size={500}>{t('tiles.sidebar.header')}</SidebarHeader>
            <SidebarFilterSection header={t('tiles.sidebar.categories')}>
                <SidebarCategoryDropdownWrapper>
                    <SidebarCategoryDropdown 
                        selectedOptions={selectedCategories}
                        onOptionSelect={onCategorySelect}
                        multiselect
                        value={selectedCategories.join(', ')}
                        placeholder={t('tiles.sidebar.categoriesDropdownPlaceholder')}
                        clearable
                    >
                        {categories.map((category) =>
                            <Option text={category} value={category} key={`dropdown-category-${category}`}>
                                <Text>{category}</Text>
                            </Option>
                        )}
                    </SidebarCategoryDropdown>
                </SidebarCategoryDropdownWrapper>
            </SidebarFilterSection>
            <SidebarFilterSection header={t('tiles.sidebar.price')}>
                <SidebarPriceContainer>
                    <SidebarPriceInput 
                        type='number' 
                        min='0' 
                        placeholder={t('tiles.sidebar.minPrice')}
                        value={String(minPrice)}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => processPriceChange(Number(e.currentTarget.value), 'min')} />
                    <Text>-</Text>
                    <SidebarPriceInput type='number' min='0' placeholder={t('tiles.sidebar.maxPrice')} />
                </SidebarPriceContainer>
            </SidebarFilterSection>
        </SidebarWrapper>
    );
}