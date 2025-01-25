import { Text, Tooltip, Button, Divider } from "@fluentui/react-components";
import { BuildingRetailShield24Regular, Edit24Regular, Delete24Regular } from "@fluentui/react-icons";
import { useTranslation } from "react-i18next";
import { ReviewDisplayContainer, ReviewTitleWrapper } from "./ParticularReviewDisplay.styled";

export type Review = {
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

export interface ParticularReviewDisplayProps {
    review: Review;
    loggedInUserId: string | null;
    handleOpenEditDialog: (review: Review) => void;
    handleDeleteReview: (reviewId: string) => void;
}

export const ParticularReviewDisplay = ({
    review,
    loggedInUserId,
    handleOpenEditDialog,
    handleDeleteReview,
} : ParticularReviewDisplayProps) => {

    const {t} = useTranslation();

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

    return(<ReviewDisplayContainer>
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
    </ReviewDisplayContainer>);
};