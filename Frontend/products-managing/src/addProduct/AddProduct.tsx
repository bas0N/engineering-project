
import { ChangeEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { 
    Button, 
    Dropdown,
    DropdownProps, 
    Option,
    Input,
    Text,
    Toast,
    ToastTitle,
    useToastController,
} from "@fluentui/react-components";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { 
    AddProductHeader, 
    AddProductWrapper, 
    AddProductInput,
    AddProductTextarea,
    AddProductInputWrapper,
    AddProductInputText,
    AddProductButtonsWrapper,
    AddProductAfterCreationActivites
} from "./AddProduct.styled";
import { Categories } from "./categories/Categories";
import { Features } from "./features/Features";
import { Details } from './details/Details';
import { Finalization } from './finalization/Finalization';

import '../i18n/i18n';

const AddProduct = () => {
    const {t} = useTranslation();

    const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'localToaster' : 'mainToaster';
    const token = localStorage.getItem('authToken');

    const [title, setTitle] = useState('');
    const [categories, setCategories] = useState<string[]>([]);
    const [mainCategory, setMainCategory] = useState('');
    const [features, setFeatures] = useState<string[]>([]);
    const [description, setDescription] = useState('');
    const [details, setDetails] = useState<Record<string,string>>({});
    const [price, setPrice] = useState(0);
    const [store, setStore] = useState('');
    const [finalizingProduct, setFinalizingProduct] = useState(false);
    const [productId, setProductId] = useState('');
    const {dispatchToast} = useToastController(toasterId);
    const navigate = useNavigate();

    const handleAddingProduct = async() => {
        try {
            setProductId('');
            const result = await axios.post(`${import.meta.env.VITE_API_URL}product`, {
                title,
                price,
                mainCategory,
                store,
                description: description.split('\n'),
                details,
                features,
                categories
            }, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            setProductId(result.data.parentAsin);
        } catch {
            setProductId('');
            dispatchToast(<Toast>
                <ToastTitle>{t('addProduct.addingFailure')}</ToastTitle>
            </Toast>, {intent: 'error', position: 'top-start'})
        }
    };

    const onOptionSelect: DropdownProps['onOptionSelect'] = (_ev, data) => {
        setMainCategory(data.optionText ?? "");
    };

    if (productId !== '') {
        return (
            <AddProductWrapper>
                <AddProductHeader as='h2'>
                    {t('addProduct.productAdded')}
                </AddProductHeader>
                <AddProductAfterCreationActivites>
                    <Button onClick={() => navigate('/products/mine')}>
                        {t('addProduct.userProductsNavigation')}
                    </Button>
                    <Button onClick={() => navigate(`/products/images/${productId}`)}>
                        {t('addProduct.addImages')}
                    </Button>
                    <Button onClick={() => navigate(`/products/mine/${productId}`)}>
                        {t('addProduct.productInspection')}
                    </Button>
                </AddProductAfterCreationActivites>
            </AddProductWrapper>
        )
    }

    return <AddProductWrapper>
        {
            finalizingProduct ? (<>
                <AddProductHeader as='h2'>
                    {t('addProduct.finalizationHeader')}
                </AddProductHeader>
                <Finalization 
                    title={title}
                    description={description}
                    categories={categories}
                    details={details}
                    features={features}
                />
                <AddProductInputWrapper>
                    <AddProductInputText>
                        {t('addProduct.store')}
                    </AddProductInputText>
                    <Input 
                        value={store}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setStore(e.currentTarget.value)}
                        placeholder={t('addProduct.storePlaceholder')} 
                    />
                </AddProductInputWrapper>
                <AddProductInputWrapper>
                    <AddProductInputText>
                        {t('addProduct.price')}
                    </AddProductInputText>
                    <Input 
                        value={String(price)}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setPrice(Number(e.currentTarget.value))}
                        type='number' 
                        min='0' 
                        placeholder={t('addProduct.pricePlaceholder')} 
                    />
                </AddProductInputWrapper>
                <AddProductInputWrapper>
                    <AddProductInputText>
                        {t('addProduct.mainCategory')}
                    </AddProductInputText>
                    <Dropdown
                        value={mainCategory}
                        selectedOptions={[mainCategory]}
                        onOptionSelect={onOptionSelect}
                    >
                        {categories.map((category) => (<Option 
                            value={category} 
                            text={category} 
                            key={`main-category-${category}`}>
                                <Text>{category}</Text>
                        </Option>))}
                    </Dropdown>
                </AddProductInputWrapper>
                <AddProductButtonsWrapper>
                    <Button onClick={() => setFinalizingProduct(false)}>
                        {t('addProduct.goBack')}
                    </Button>
                    <Button onClick={() => handleAddingProduct()}>
                        {t('addProduct.finalizeProductCreation')}
                    </Button>
                </AddProductButtonsWrapper>
            </>) : (<>
                <AddProductHeader as='h2'>
                    {t('addProduct.title')}
                </AddProductHeader>
                <AddProductInput 
                    value={title}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.currentTarget.value)}
                    appearance='underline'
                    placeholder={t('addProduct.titlePlaceholder')} 
                />
                <AddProductTextarea 
                    placeholder={t('addProduct.descriptionPlaceholder')}
                    value={description}
                    onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.currentTarget.value)}
                />
                <Categories 
                    categories={categories} 
                    setCategories={setCategories} 
                />
                <Features 
                    features={features}
                    setFeatures={setFeatures}
                />
                <Details
                    details={details}
                    setDetails={setDetails}
                />
                <Button onClick={() => setFinalizingProduct(true)}>
                    {t('addProduct.finalizeCreation')}
                </Button>
            </>)
        }
    </AddProductWrapper>
};

export default AddProduct;