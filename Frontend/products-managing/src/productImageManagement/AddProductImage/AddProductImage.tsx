import { ChangeEvent, useState, useRef } from 'react';
import { 
    Button, 
    Spinner,
    Text,
    Toast,
    ToastTitle,
    useToastController,
} from '@fluentui/react-components';
import { useTranslation } from 'react-i18next';
import { 
    ImagesAddingWrapper, 
    ImagesAddingClosingButton,
    ImagesAddingFileInput,
    ImagesAddingContainer,
    ImagesAddingButton 
} from './AddProductImage.styled';
import { DismissRegular } from '@fluentui/react-icons';
import axios from 'axios';

interface AddingImagesProps {
    productId: string;
    toasterId: string;
    closePanel: () => void;
}

export const AddProductImage = ({
    productId,
    toasterId,
    closePanel
} : AddingImagesProps) => {

    const {t} = useTranslation();
    const token = localStorage.getItem('authToken');
    const imageInputRef = useRef<HTMLInputElement>(null);

    const {dispatchToast} = useToastController(toasterId);

    const [imageFile, setImageFile] = useState<File | null>(null);
    const [sending, setSending] = useState(false);

    const handleImageFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        const imageData = e.currentTarget.files?.[0] || null;

        if(imageData){
            setImageFile(imageData);
        }
    };

    const uploadImage = async() => {
        try {
            setSending(true);
            const formData = new FormData();
            formData.append('hi_res', imageFile as File);
            formData.append('large', imageFile as File);
            formData.append('thumb', imageFile as File);
            formData.append('productId', productId);
            formData.append('variant', 'hi_res');
            await axios.post(`${import.meta.env.VITE_API_URL}product/${productId}/image`, 
                formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });
            dispatchToast(<Toast>
                <ToastTitle>{t('productImageManagement.addingProduct.productSuccessfullyAdded')}</ToastTitle>
            </Toast>, {position: 'top-end', intent: 'success'});
            closePanel();
            setSending(false);
        } catch {
            dispatchToast(<Toast>
                <ToastTitle>{t('productImageManagement.addingProduct.uploadFailed')}</ToastTitle>
            </Toast>, {position: 'top-end', intent: 'error'});
            setSending(false);
        }
    }

    const triggerImageFileChange = () => {
        imageInputRef?.current?.click();
    }

    return (<ImagesAddingWrapper>
        <ImagesAddingClosingButton 
            aria-label={t('productImageManagement.addingProduct.closeButton')}
            onClick={() => closePanel()}
        >
            <DismissRegular />
        </ImagesAddingClosingButton>
        <Text as='h2' size={700}>
            {t('productImageManagement.addingProduct.header')}
        </Text>
        <ImagesAddingContainer>
            <ImagesAddingButton onClick={() => triggerImageFileChange()}>
                {t('productImageManagement.addingProduct.load')}
            </ImagesAddingButton>
            {
                sending ? (<Spinner label={t('productImageManagement.addingProduct.loadingProduct')} />)
                : (
                    <Text size={400}>
                        {imageFile === null ? t('productImageManagement.addingProduct.loadProduct')
                        : t('productImageManagement.addingProduct.productLoaded')}
                    </Text>
                )
            }
        </ImagesAddingContainer>
        <ImagesAddingFileInput 
            ref={imageInputRef}
            type='file'
            accept='images/*'
            onChange={handleImageFileChange}
            aria-label={t('productImageManagement.addingProduct.imageInput')}
        />
        <Button 
            disabled={imageFile === null}
            onClick={() => uploadImage()}
        >
            {t('productImageManagement.addingProduct.upload')}
        </Button>
    </ImagesAddingWrapper>);
}