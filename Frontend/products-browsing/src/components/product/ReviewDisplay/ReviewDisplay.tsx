import { useTranslation } from 'react-i18next';
import {
    Spinner,
    Text,
} from '@fluentui/react-components';
import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {
    ReviewDisplayWrapper,
} from './ReviewDisplay.styled';
import { ReviewPagination } from './components/ReviewPagination/ReviewPagination';
import { Review, ParticularReviewDisplay } from './components/ParticularReviewDisplay/ParticularReviewDisplay';
import { ReviewEditDialog } from './components/ReviewEditDialog/ReviewEditDialog';

interface ReviewDisplayProps {
    productId: string;
    token: string;
    reloadTriggerer: boolean;
}

export const ReviewDisplay = ({
    productId,
    token,
    reloadTriggerer
}: ReviewDisplayProps) => {

    const {t} = useTranslation();

    const [reviews, setReviews] = useState<Review[] | null>(null);

    const [pageNumber, setPageNumber] = useState(1);

    const [loggedInUserId, setLoggedInUserId] = useState<string | null>(null);

    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
    const [editReviewId, setEditReviewId] = useState<string | null>(null);
    const [editTitle, setEditTitle] = useState('');
    const [editText, setEditText] = useState('');
    const [editRating, setEditRating] = useState(1);

    const fetchLoggedInUserId = useCallback(async () => {
        try {
            const response = await axios.get(`${import.meta.env.VITE_API_URL}auth/user/my-userId`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setLoggedInUserId(response.data);
        } catch (error) {
            console.error('Failed to fetch logged-in user ID', error);
        }
    }, [token]);

    const processLeftPage = () => {
        if (pageNumber - 1 > 0 && reviews !== null) {
            setPageNumber((number) => number - 1);
        }
    };

    const processRightPage = () => {
        // If the length of reviews is exactly 10, it suggests more pages
        if (reviews !== null && reviews.length === 10) {
            setPageNumber((number) => number + 1);
        }
    };

    const leftPaginationDisabled = pageNumber === 1 || reviews === null;
    const rightPaginationDisabled = reviews === null || reviews.length < 10;

    // Fetch reviews from the backend
    const getReviewsData = useCallback(async () => {
        setReviews(null);
        try {
            const result = await axios.get(`${import.meta.env.VITE_API_URL}product/review/${productId}/reviews`, {
                params: {
                    page: pageNumber - 1
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
        fetchLoggedInUserId();
    }, [fetchLoggedInUserId]);

    useEffect(() => {
        getReviewsData();
    }, [reloadTriggerer, getReviewsData]);

    const handleDeleteReview = async (reviewId: string) => {
        try {
            await axios.delete(`${import.meta.env.VITE_API_URL}product/review/${reviewId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            // Refresh the review list
            getReviewsData();
        } catch (error) {
            console.error('Failed to delete review:', error);
        }
    };

    const handleOpenEditDialog = (review: Review) => {
        setEditReviewId(review.id);
        setEditTitle(review.title);
        setEditText(review.text);
        setEditRating(review.rating);
        setIsEditDialogOpen(true);
    };

    const handleCloseEditDialog = () => {
        setIsEditDialogOpen(false);
        setEditReviewId(null);
    };

    const handleSaveEdit = async () => {
        if (!editReviewId) return;

        try {
            const requestBody = {
                title: editTitle,
                text: editText,
                rating: editRating,
            };
            await axios.put(`${import.meta.env.VITE_API_URL}product/review/${editReviewId}`, requestBody, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            getReviewsData();
            handleCloseEditDialog();
        } catch (error) {
            console.error('Failed to update review:', error);
        }
    };

    return (
        <ReviewDisplayWrapper>
            <Text as='h2' size={700} weight='semibold'>
                {t('product.reviews.reviewsTitle')}
            </Text>
            {
                reviews === null ? (
                    <Spinner
                        size='large'
                        label={t('product.reviews.loadingReviews')}
                        labelPosition="after"
                    />
                ) : (
                    <>
                        {
                            reviews.map((review) => (
                                <ParticularReviewDisplay 
                                    key={`review-${review.id}`} 
                                    review={review}
                                    loggedInUserId={loggedInUserId}
                                    handleOpenEditDialog={handleOpenEditDialog}
                                    handleDeleteReview={handleDeleteReview}
                                />
                            ))
                        }
                        <ReviewPagination
                            processLeftPage={processLeftPage}
                            processRightPage={processRightPage}
                            pageNumber={pageNumber}
                            leftPaginationDisabled={leftPaginationDisabled}
                            rightPaginationDisabled={rightPaginationDisabled}
                        />
                    </>
                )
            }

            <ReviewEditDialog 
                isEditDialogOpen={isEditDialogOpen}
                setEditRating={setEditRating}
                setEditText={setEditText}
                setEditTitle={setEditTitle}
                handleCloseEditDialog={handleCloseEditDialog}
                handleSaveEdit={handleSaveEdit}
                editRating={editRating}
                editText={editText}
                editTitle={editTitle} 
                setIsEditDialogOpen={setIsEditDialogOpen} 
            />
        </ReviewDisplayWrapper>
    );
};
