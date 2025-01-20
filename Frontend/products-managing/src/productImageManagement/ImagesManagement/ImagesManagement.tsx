import { Suspense } from 'react';
import { useTranslation } from 'react-i18next';
import { 
    Button, 
    Spinner, 
    Text,
    Toast,
    ToastTitle,
    useToastController
} from '@fluentui/react-components';
import { DeleteRegular } from '@fluentui/react-icons';
import axios from 'axios';

import { ImageType } from '../ProductImageManagement';
import { 
    ImagesManagementWrapper,
    ImageContainer,
    ImageThumb,
    ImageOperations
} from './ImagesManagement.styled';

interface ImagesManagementProps {
    images: ImageType[];
    toasterId: string;
    productId: string;
    reloadData: () => void;
}

export const ImagesManagement = ({
    images,
    toasterId,
    productId,
    reloadData
}: ImagesManagementProps) => {

    const {t} = useTranslation();
    const {dispatchToast} = useToastController(toasterId);
    const token = localStorage.getItem('authToken');

    const deleteImage = async(ind: number) => {
        try {
            await axios.delete(`${import.meta.env.VITE_API_URL}product/${productId}/image`,
            {
                data: {
                    productId: productId,
                    order: ind
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            dispatchToast(<Toast>
                <ToastTitle>
                    {t('productImageManagement.imagesDisplay.deletionSuccessful')}
                </ToastTitle>
            </Toast>, {intent: 'success', position: 'top-end'});
            reloadData();
        } catch (error) {
            console.log(error);
            dispatchToast(<Toast>
                <ToastTitle>
                    {t('productImageManagement.imagesDisplay.deletionError')}
                </ToastTitle>
            </Toast>, {intent: 'error', position: 'top-end'});
        }
    };

    return (<ImagesManagementWrapper>
        <Text as='h3' size={600} weight='semibold'>
            {t('productImageManagement.imagesDisplay.header')}
        </Text>
        {
            images.map((image, ind) => <ImageContainer key={image.hiRes}>
                <Suspense fallback={<Spinner label={t('productImageManagement.imagesDisplay.imageLoading')} />}>
                    <ImageThumb 
                        src={image.thumb} 
                        alt={`${t('productImageManagement.imagesDisplay.image')}-${ind+1}`}
                    />
                    <ImageOperations>
                        <Button 
                            aria-label={t('productImageManagement.imagesDisplay.deleteImage')}
                            appearance='subtle'
                            onClick={() => deleteImage(ind)}
                        >
                            <DeleteRegular />
                        </Button>
                    </ImageOperations>
                </Suspense>
            </ImageContainer>)
        }
    </ImagesManagementWrapper>);
}