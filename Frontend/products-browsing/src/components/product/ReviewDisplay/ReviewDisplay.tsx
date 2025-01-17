import {useTranslation} from 'react-i18next';
import {
    Button,
    Divider,
    Spinner,
    Text,
    Tooltip,
    Input,
    Dialog,
    DialogSurface,
    DialogTitle,
    DialogContent,
    DialogActions, Field
} from '@fluentui/react-components';
import {
    BuildingRetailShield24Regular,
    ChevronDoubleLeft20Regular,
    ChevronDoubleRight20Regular,
    Edit24Regular,
    Delete24Regular
} from '@fluentui/react-icons';
import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {
    ReviewDisplayWrapper,
    ReviewDisplayContainer,
    ReviewPaginationWrapper,
    ReviewTitleWrapper,
    ReviewPaginationPageDisplay
} from './ReviewDisplay.styled';

interface ReviewDisplayProps {
    productId: string;
    token: string;
    reloadTriggerer: boolean;
}

type Review = {
    id: string;
    title: string;
    text: string;
    rating: number;        // CHANGED
    userFirstName: string | null;
    userLastName: string | null;
    userId: string;
    timestamp: string;
    helpfulVote: number;
    verifiedPurchase: boolean;
};

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
            console.log('Fetched Logged-in User ID:', response.data);
        } catch (error) {
            console.error('Failed to fetch logged-in user ID', error);
        }
    }, [token]);

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
    };

    const getReviewSubheader = (review: Review) => {
        const firstPart = review.userLastName === null || review.userFirstName === null
            ? t('product.reviews.anonymusUser')
            : `${review.userFirstName}-${review.userLastName}`;
        const datePart = convertDateToHumanReadableFormat(review.timestamp);
        return `${firstPart}, ${datePart}`;
    };

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
        console.log('Logged-in User ID:', loggedInUserId);
    }, [fetchLoggedInUserId]);

    useEffect(() => {
        getReviewsData();
        console.log('Reviews:', reviews);
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
                                <ReviewDisplayContainer key={`review-${review.id}`}>
                                    <ReviewTitleWrapper>
                                        <Text as='h3' size={500} weight='semibold'>{review.title}</Text>
                                        {
                                            review.verifiedPurchase && (
                                                <Tooltip
                                                    relationship='label'
                                                    content={t('product.reviews.verifiedPurchase')}
                                                >
                                                    <BuildingRetailShield24Regular/>
                                                </Tooltip>
                                            )
                                        }
                                        {
                                            loggedInUserId === review.userId && (
                                                <div style={{display: 'flex', gap: '8px'}}>
                                                    <Tooltip
                                                        relationship='label'
                                                        content={t('product.reviews.editReviewTooltip')}
                                                    >
                                                        <Button
                                                            icon={<Edit24Regular/>}
                                                            appearance='subtle'
                                                            onClick={() => handleOpenEditDialog(review)}
                                                        />
                                                    </Tooltip>
                                                    <Tooltip
                                                        relationship='label'
                                                        content={t('product.reviews.deleteReviewTooltip')}
                                                    >
                                                        <Button
                                                            icon={<Delete24Regular/>}
                                                            appearance='subtle'
                                                            onClick={() => handleDeleteReview(review.id)}
                                                        />
                                                    </Tooltip>
                                                </div>
                                            )
                                        }
                                    </ReviewTitleWrapper>
                                    <Text as='h4'>
                                        {getReviewSubheader(review)}
                                    </Text>
                                    <Text as='span' style={{fontWeight: 600}}>
                                        {t('product.reviews.ratingLabel')}: {review.rating}/5
                                    </Text>
                                    <Text as='p'>{review.text}</Text>
                                    <Divider/>
                                </ReviewDisplayContainer>
                            ))
                        }
                        <ReviewPaginationWrapper data-testid="pagination-section">
                            <Button
                                icon={<ChevronDoubleLeft20Regular/>}
                                appearance='subtle'
                                onClick={processLeftPage}
                                disabled={leftPaginationDisabled}
                            />
                            <ReviewPaginationPageDisplay>
                                {pageNumber}
                            </ReviewPaginationPageDisplay>
                            <Button
                                icon={<ChevronDoubleRight20Regular/>}
                                appearance='subtle'
                                onClick={processRightPage}
                                disabled={rightPaginationDisabled}
                            />
                        </ReviewPaginationWrapper>
                    </>
                )
            }

            <Dialog open={isEditDialogOpen} onOpenChange={(_, data) => setIsEditDialogOpen(data.open)}>
                <DialogSurface>
                    <DialogTitle>{t('product.reviews.editDialogTitle')}</DialogTitle>
                    <DialogContent>
                        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
                            <Field label={t('product.reviews.titleInputLabel')}>
                                <Input
                                    value={editTitle}
                                    onChange={(e) => setEditTitle(e.target.value)}
                                />
                            </Field>

                            <Field label={t('product.reviews.textInputLabel')}>
                                <Input
                                    value={editText}
                                    onChange={(e) => setEditText(e.target.value)}
                                />
                            </Field>

                            <Field label={t('product.reviews.ratingLabel')}>
                                <Input
                                    type="number"
                                    value={editRating.toString()}
                                    onChange={(e) => setEditRating(Number(e.target.value))}
                                />
                            </Field>
                        </div>
                    </DialogContent>

                    <DialogActions>
                        <Button appearance='secondary' onClick={handleCloseEditDialog}>
                            {t('common.cancel')}
                        </Button>
                        <Button appearance='primary' onClick={handleSaveEdit}>
                            {t('common.save')}
                        </Button>
                    </DialogActions>
                </DialogSurface>
            </Dialog>
        </ReviewDisplayWrapper>
    );
};
