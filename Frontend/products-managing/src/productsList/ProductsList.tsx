import { useEffect, useState } from 'react';
import { 
    Spinner,
    Toast, 
    ToastTitle, 
    useToastController 
} from '@fluentui/react-components';
import axios from 'axios';
import { useTranslation } from 'react-i18next';
import { 
    ProductsWrapper,
    ProductsWrapperHeader 
} from './ProductsList.styled';
import { ProductsPager } from './productsPager/ProductsPager';
import { Product, ProductsDisplay } from './productsDisplay/ProductsDisplay';

export const ProductsList = () => {

    const {t} = useTranslation();
    const [currentPage, setCurrentPage] = useState(0);
    const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'mainToaster' : 'localToaster';
    const token = localStorage.getItem('authToken');
    const { dispatchToast } = useToastController(toasterId);
    const [items, setItems] = useState<Product[] | null>(null);

    const handlePageChange = (newPage: number) => {
        setCurrentPage(newPage)
    };

    const deleteProduct = async(productId: string) => {
        try {
            await axios.delete(`${import.meta.env.VITE_API_URL}product/${productId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                }
            })
            setItems((items) => items === null ? null : (items as Product[]).filter((item) => item.parentAsin !== productId));
            dispatchToast(<Toast>
                <ToastTitle>{t('productsList.deletionSuccessful')}</ToastTitle>
            </Toast>, {intent: 'success', position: 'top-end'});
        } catch {
            dispatchToast(<Toast>
                <ToastTitle>{t('productsList.deletingError')}</ToastTitle>
            </Toast>, {intent: 'error', position: 'top-end'})
        }
    };

    useEffect(() => {
        const getProductsList = async() => {
            try {
                const results = await axios.get(`${import.meta.env.VITE_API_URL}product/my-products`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                setItems(results.data.content);
            } catch {
                dispatchToast(<Toast>
                    <ToastTitle>{t('productsList.loadingError')}</ToastTitle>
                </Toast>, {intent: 'error', position: 'top-end'})
            }
        }

        getProductsList();
    }, [dispatchToast, t, token]);

    return <ProductsWrapper>
        <ProductsWrapperHeader>
            {t('productsList.header')}
        </ProductsWrapperHeader>
        {
            items === null ? (<Spinner 
                label={t('productsList.loading')} 
                size='large'
            />)
            : (<ProductsDisplay 
                products={items as Product[]} 
                deleteProduct={deleteProduct}
            />)
        }
        <ProductsPager
            page={currentPage}
            changePage={handlePageChange}
            currentProductsNumber={items !== null ? items.length : 0}
        />
    </ProductsWrapper>
};

export default ProductsList;