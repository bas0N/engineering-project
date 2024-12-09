import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import axios from 'axios';
import { 
    InputProps,
    Rating, 
    Text, 
    TextareaProps 
} from '@fluentui/react-components';
import { Dismiss32Regular } from '@fluentui/react-icons';
import { 
    ReviewCloseButton,
    ReviewFormWrapper, 
    ReviewFormContainer, 
    ReviewFormHeader,
    ReviewFormTitle,
    ReviewFormSection,
    ReviewFormOpinion,
    ReviewFormOpinionWrapper,
    ReviewFormButton,
 } from './ReviewForm.styled';

interface ReviewFormProps {
    productId: string;
    token: string;
    closeReviewForm: () => void;
}

export const ReviewForm = ({
    productId,
    token,
    closeReviewForm
} : ReviewFormProps) => {
    const {t} = useTranslation();
    const MAX_OPINION_LENGTH = 800;
    const MAX_TITLE_LENGTH = 300;

    const [ratingValue, setRatingValue] = useState(0);
    const [textOpinion, setTextOpinion] = useState('');
    const [title, setTitle] = useState('');

    const onOpinionChange: TextareaProps["onChange"] = (_ev, data) => {
        if (data.value.length <= MAX_OPINION_LENGTH) {
            setTextOpinion(data.value);
        }
    };

    const onTitleChange: InputProps["onChange"] = (_ev, data) => {
        if(data.value.length <= MAX_TITLE_LENGTH) {
            setTitle(data.value);
        }
    }

    const processAddingReview = async() => {
        try {
            const result = await axios.post(`${import.meta.env.VITE_API_URL}product/review/${productId}`, {
                rating: ratingValue,
                title,
                text: textOpinion
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log(result);
            closeReviewForm();
        } catch (error) { 
            console.log(error);
        }
    }

    const addingButtonDisabled = ratingValue === 0 
        || textOpinion.length === 0
        || title.length === 0;

    return (
        <ReviewFormWrapper>
            <ReviewCloseButton onClick={closeReviewForm}>
                <Dismiss32Regular />
            </ReviewCloseButton>
            <ReviewFormContainer>
                <ReviewFormHeader>
                    {t('product.addReviewHeader')}
                </ReviewFormHeader>
                <ReviewFormSection>
                    <Rating 
                        value={ratingValue}
                        onChange={(_, data) => setRatingValue(data.value)}
                        size="large"
                        step={0.5}
                    />
                </ReviewFormSection>
                <Text as='h3'>
                    {t('product.shareOpinionHeader')}
                </Text>
                <ReviewFormOpinionWrapper>
                    <ReviewFormTitle 
                        type='text' 
                        value={title} 
                        onChange={onTitleChange} 
                        placeholder={t('product.opinionTitle')}
                    />
                    <Text>
                        {title.length}/{MAX_TITLE_LENGTH}
                    </Text>
                </ReviewFormOpinionWrapper>
                <ReviewFormOpinionWrapper>
                    <ReviewFormOpinion 
                        placeholder={t('product.opinionPlaceholder')}
                        value={textOpinion} 
                        onChange={onOpinionChange} 
                    />
                    <Text>
                        {textOpinion.length}/{MAX_OPINION_LENGTH}
                    </Text>
                </ReviewFormOpinionWrapper>
                <ReviewFormButton 
                    onClick={processAddingReview}
                    disabled={addingButtonDisabled}
                >
                    {t('product.addReview')}
                </ReviewFormButton>
            </ReviewFormContainer>
        </ReviewFormWrapper>
    )
}