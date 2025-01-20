import { Button, Text } from '@fluentui/react-components';
import { 
    ChevronLeftRegular,
    ChevronRightRegular 
} from '@fluentui/react-icons';
import { ProductsPagerWrapper } from "./ProductsPager.styled";
import { useTranslation } from 'react-i18next';

export interface ProductsPagerProps {
    page: number;
    currentProductsNumber: number;
    changePage: (newPage: number) => void;
}

export const ProductsPager = ({
    page,
    currentProductsNumber,
    changePage
}:ProductsPagerProps) => {
    const {t} = useTranslation();
    
    return (
        <ProductsPagerWrapper>
            <Button 
                aria-label={t('productsList.leftPager')}
                disabled={page === 0} 
                onClick={() => page > 0 && changePage(page-1)}
            >
                <ChevronLeftRegular />
            </Button>
            <Text size={500}>
                {page+1}
            </Text>
            <Button 
                aria-label={t('productsList.rightPager')}
                disabled={currentProductsNumber < 10}
                onClick={() => changePage(page+1)}
            >
                <ChevronRightRegular />
            </Button>
        </ProductsPagerWrapper>
    );
}