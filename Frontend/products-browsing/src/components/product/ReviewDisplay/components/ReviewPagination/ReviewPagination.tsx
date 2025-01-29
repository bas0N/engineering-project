import { Button } from "@fluentui/react-components";
import { ChevronDoubleLeft20Regular, ChevronDoubleRight20Regular } from "@fluentui/react-icons";
import { ReviewPaginationWrapper, ReviewPaginationPageDisplay } from "./ReviewPagination.styled";
import { useTranslation } from "react-i18next";

export interface ReviewPaginationProps {
    pageNumber: number;
    leftPaginationDisabled: boolean;
    rightPaginationDisabled: boolean;
    processRightPage: () => void;
    processLeftPage: () => void;
}

export const ReviewPagination = ({
    pageNumber,
    leftPaginationDisabled,
    rightPaginationDisabled,
    processRightPage,
    processLeftPage
}:ReviewPaginationProps) => {

    const {t} = useTranslation();

    return (<ReviewPaginationWrapper data-testid="pagination-section">
        <Button
            aria-label={t('products.reviews.leftPagination')}
            icon={<ChevronDoubleLeft20Regular/>}
            appearance='subtle'
            onClick={processLeftPage}
            disabled={leftPaginationDisabled}
        />
        <ReviewPaginationPageDisplay>
            {pageNumber}
        </ReviewPaginationPageDisplay>
        <Button
            aria-label={t('products.reviews.rightPagination')}
            icon={<ChevronDoubleRight20Regular/>}
            appearance='subtle'
            onClick={processRightPage}
            disabled={rightPaginationDisabled}
        />
    </ReviewPaginationWrapper>);
};