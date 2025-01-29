import { 
    Dialog, 
    DialogSurface, 
    DialogTitle, 
    DialogContent, 
    Field, 
    Input,
    DialogActions, 
    Button 
} from "@fluentui/react-components";
import { useTranslation } from "react-i18next"
import { DialogContentWrapper } from "./ReviewEditDialog.styled";

export interface ReviewEditDialogProps {
    isEditDialogOpen: boolean;
    editTitle: string;
    setEditTitle: (newTitle: string) => void;
    editText: string;
    setEditText: (newText: string) => void;
    editRating: number;
    setEditRating: (newRating: number) => void;
    handleCloseEditDialog: () => void;
    handleSaveEdit: () => void;
    setIsEditDialogOpen: (isOpen: boolean) => void;
}

export const ReviewEditDialog = ({
    isEditDialogOpen,
    editRating,
    editText,
    editTitle,
    setEditRating,
    setEditText,
    setEditTitle,
    handleCloseEditDialog,
    handleSaveEdit,
    setIsEditDialogOpen
}: ReviewEditDialogProps) => {
    const {t} = useTranslation();

    return (<Dialog open={isEditDialogOpen} onOpenChange={(_, data) => setIsEditDialogOpen(data.open)}>
        <DialogSurface>
            <DialogTitle>{t('product.reviews.editDialogTitle')}</DialogTitle>
            <DialogContent>
                <DialogContentWrapper>
                    <Field label={t('product.reviews.titleInputLabel')}>
                        <Input
                            placeholder={t('product.reviews.titleInputLabel')}
                            value={editTitle}
                            onChange={(e) => setEditTitle(e.target.value)}
                        />
                    </Field>

                    <Field label={t('product.reviews.textInputLabel')}>
                        <Input
                            placeholder={t('product.reviews.textInputLabel')}
                            value={editText}
                            onChange={(e) => setEditText(e.target.value)}
                        />
                    </Field>

                    <Field label={t('product.reviews.ratingLabel')}>
                        <Input
                            type="number"
                            placeholder={t('product.reviews.ratingLabel')}
                            value={editRating.toString()}
                            onChange={(e) => setEditRating(Number(e.target.value))}
                        />
                    </Field>
                </DialogContentWrapper>
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
    );
}