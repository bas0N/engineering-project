import { useTranslation } from 'react-i18next';
import { Button, Divider, Spinner, Text, Tooltip } from '@fluentui/react-components';
import { 
    BuildingRetailShield24Regular,
    ChevronDoubleLeft20Regular,
    ChevronDoubleRight20Regular
} from '@fluentui/react-icons';
import { useCallback, useEffect, useState } from 'react';
import { 
    ReviewDisplayWrapper, 
    ReviewDisplayContainer, 
    ReviewPaginationWrapper,
    ReviewTitleWrapper,
    ReviewPaginationPageDisplay,
} from './ReviewDisplay.styled';
import axios from 'axios';

interface ReviewDisplayProps {
    productId: string;
    token: string;
    reloadTriggerer: boolean;
}

type Review = {
    id: string;
    title: string;
    text: string;
    userFirstName: string | null;
    userLastName: string | null;
    userId: string;
    timestamp: string;
    helpfulVote: number;
    verifiedPurchase: boolean;
}

export const ReviewDisplay = ({
    productId,
    token,
    reloadTriggerer
} : ReviewDisplayProps) => {

    const {t} = useTranslation();

    const [reviews, setReviews] = useState<Review[]|null>(null);
    const [pageNumber, setPageNumber] = useState(1);

    const convertDateToHumanReadableFormat = (date: string) => {
        const newDate = new Date(date);
        const readableForm = newDate.toLocaleString('en-US', {
            weekday: 'long',
            year: 'numeric', 
            month: 'long', 
            day: 'numeric', 
            hour: 'numeric',
            minute: '2-digit',
            second: '2-digit', 
            timeZoneName: 'short' 
        });
        return readableForm;
    }

    const getReviewSubheader = (review: Review) => {
        const firstPart = review.userLastName === null || review.userFirstName === null
            ? t('product.reviews.anonymusUser')
            : `${review.userFirstName}-${review.userLastName}`;
        const datePart = convertDateToHumanReadableFormat(review.timestamp);
        return `${firstPart}, ${datePart}`;
    }

    const processLeftPage = () => {
        if(pageNumber - 1 > 0 && reviews !== null){
            setPageNumber((number) => number-=1);
        }
    };

    const processRightPage = () => {
        if(reviews !== null && reviews.length === 10){
            setPageNumber((number) => number+=1);
        }
    }

    const leftPaginationDisabled = pageNumber === 1 || reviews === null;
    const rightPaginationDisabled = reviews === null || reviews.length < 10;

    const getReviewsData = useCallback(async() => {
        setReviews(null);
        try {
            const result = await axios.get(`${import.meta.env.VITE_API_URL}product/review/${productId}/reviews`, {
                params: {
                    page: pageNumber-1
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setReviews(result.data.content);
        } catch (error) {
            console.log(error);
        }
    }, [pageNumber, productId, token]);

    useEffect(() => {
        getReviewsData();
    }, [reloadTriggerer, getReviewsData]);

    return (
        <ReviewDisplayWrapper>
            <Text as='h2' size={700} weight='semibold'>
                {t('product.reviews.reviewsTitle')}
            </Text>
            {
                reviews === null ?
                    <Spinner
                        size='large' 
                        label={t('product.reviews.loadingReviews')}
                        labelPosition="after"
                    />
                : <>
                    {
                        reviews.map((review) => (
                            <ReviewDisplayContainer key={`review-${review.id}`}>
                                <ReviewTitleWrapper>
                                    <Text as='h3' size={500} weight='semibold'>{review.title}</Text>
                                    {
                                        review.verifiedPurchase && (
                                            <Tooltip relationship='label' content={t('product.reviews.verifiedPurchase')}>
                                                <BuildingRetailShield24Regular /> 
                                            </Tooltip>
                                        )
                                    }
                                </ReviewTitleWrapper>
                                <Text as='h4'>
                                    {getReviewSubheader(review)}
                                </Text>
                                <Text as='p'>{review.text}</Text>
                                <Divider />
                            </ReviewDisplayContainer>
                        ))
                    }
                    <ReviewPaginationWrapper data-testid="pagination-section">
                        <Button
                            icon={<ChevronDoubleLeft20Regular />}
                            appearance='subtle'
                            onClick={processLeftPage}
                            disabled={leftPaginationDisabled}
                        />
                        <ReviewPaginationPageDisplay>
                            {pageNumber}
                        </ReviewPaginationPageDisplay>
                        <Button
                            icon={<ChevronDoubleRight20Regular />}
                            appearance='subtle'
                            onClick={processRightPage}
                            disabled={rightPaginationDisabled}
                        />
                    </ReviewPaginationWrapper>
                </>
            }
        </ReviewDisplayWrapper>
    );
}