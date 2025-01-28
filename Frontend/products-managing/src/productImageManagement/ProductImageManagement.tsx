import { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { 
    Button,
    Spinner,
    Toast, 
    ToastTitle, 
    useToastController 
} from "@fluentui/react-components";
import axios from 'axios';
import { 
    ProductsImageManagementWrapper, 
    ProductsImageHeader,
    ProductsImageError
} from "./ProductImageManagement.styled";

import {AddProductImage} from './AddProductImage/AddProductImage';
import {ImagesManagement} from './ImagesManagement/ImagesManagement';

export type ImageType = {
    hiRes: string;
    large: string;
    variant: string;
    thumb: string;
}

export const ProductImageManagement = () => {

    const params = useParams();
    const {t} = useTranslation();
    const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'mainToaster' : 'localToaster';
    const token = localStorage.getItem('authToken');
    const {dispatchToast} = useToastController(toasterId);
    const [loadingError, setLoadingError] = useState(false);
    const [addingNewImage, setAddingNewImage] = useState(false);
    const [currentImages, setCurrentImages] = useState<ImageType[] | null>(null);

    const getImagesData = useCallback(async() => {
        try {
            setLoadingError(false);
            setCurrentImages(null);
            const result = await axios.get(`${import.meta.env.VITE_API_URL}product/${params.productId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setCurrentImages(result.data.images ?? []);
        } catch {
            dispatchToast(<Toast>
                <ToastTitle>{t('productImageManagement.loadingFailure')}</ToastTitle>
            </Toast>, {position: 'top-end', intent: 'error'});
            setLoadingError(true);
        }
    }, [dispatchToast, params.productId, t, token]);

    useEffect(() => {
        const execGetImagesData = async() => {
            await getImagesData();
        }
        execGetImagesData();
    }, [getImagesData]);

    const handleClosingAddingPanel = async(adding?: string) => {
        setAddingNewImage(false);
        if(adding){
            await getImagesData();
        }
    }

    if(params.productId === undefined) {
        return (<ProductsImageManagementWrapper>
            <ProductsImageHeader as='h2'>
                {t('productImageManagement.noProductFound')}
            </ProductsImageHeader>
        </ProductsImageManagementWrapper>);
    }

    if(loadingError) {
        return <ProductsImageManagementWrapper>
            <ProductsImageHeader as='h2'>
                {t('productImageManagement.images')}
            </ProductsImageHeader>
            <ProductsImageError as='h3'>
                {t('productImageManagement.error')}
            </ProductsImageError>
        </ProductsImageManagementWrapper>
    }

    return (<ProductsImageManagementWrapper>
        <ProductsImageHeader as='h2'>
            {t('productImageManagement.images')}
        </ProductsImageHeader>
        {
            loadingError ? (
                <ProductsImageError as='h3'>
                    {t('productImageManagement.error')}
                </ProductsImageError>
            ) : (<>
                {
                    currentImages === null ? (<Spinner 
                        label={t('productImageManagement.loading')}
                    />) : (<ImagesManagement 
                        toasterId={toasterId}
                        images={currentImages as ImageType[]} 
                        productId={params.productId as string}
                        reloadData={getImagesData}
                    />)
                }
                {
                    addingNewImage && (<AddProductImage 
                        productId={params.productId as string}
                        closePanel={handleClosingAddingPanel} 
                        toasterId={toasterId}
                    />)
                }
                <Button onClick={() => setAddingNewImage(true)}>
                    {t('productImageManagement.addNewImage')}
                </Button>
            </>)
        }
    </ProductsImageManagementWrapper>);
};

export default ProductImageManagement;