package ass.java6.ass.Controller;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.ProductImage;
import ass.java6.ass.Service.CategoryService;
import ass.java6.ass.Service.FileUploadService;
import ass.java6.ass.Service.ProductImageService;
import ass.java6.ass.Service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        System.out.println("listProducts: Hiển thị danh sách sản phẩm, số lượng: " + products.size());
        model.addAttribute("products", products);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/productManage";
    }

    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes,
            Model model) {
        System.out.println("createProduct: Thêm sản phẩm mới: " + product);
        System.out.println("createProduct: Số lượng ảnh: " + (images != null ? images.length : "null"));

        if (result.hasErrors()) {
            System.out.println("createProduct: Lỗi validation: " + result.getAllErrors());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/productManage";
        }

        try {
            product.setCreateDate(new Date());
            Product savedProduct = productService.save(product);
            System.out.println("createProduct: Đã lưu sản phẩm, ID: " + savedProduct.getId());

            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = fileUploadService.uploadFile(image, "products");
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imageUrl);
                        productImage.setProduct(savedProduct);
                        productImageService.save(productImage);
                        System.out.println("createProduct: Đã lưu ảnh: " + imageUrl);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println("createProduct: Lỗi khi thêm sản phẩm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/productManage";
        }
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getProduct(@PathVariable("id") Integer id) {
        System.out.println("getProduct: Lấy dữ liệu sản phẩm, ID: " + id);
        try {
            Optional<Product> productOptional = productService.findById1(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                System.out.println("getProduct: Sản phẩm tìm thấy: " + product);
                return ResponseEntity.ok(product);
            } else {
                System.out.println("getProduct: Không tìm thấy sản phẩm với ID: " + id);
                return ResponseEntity.status(404).body("Không tìm thấy sản phẩm với ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("getProduct: Lỗi khi lấy sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("showEditForm: Bắt đầu xử lý cho ID: " + id);
        try {
            Optional<Product> productOptional = productService.findById1(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                System.out.println("showEditForm: Sản phẩm tìm thấy: " + product);
                model.addAttribute("product", product);
                model.addAttribute("products", productService.findAll());
                model.addAttribute("categories", categoryService.findAll());
                return "admin/productManage";
            } else {
                System.out.println("showEditForm: Không tìm thấy sản phẩm với ID: " + id);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm với ID: " + id);
                return "redirect:/admin/products";
            }
        } catch (Exception e) {
            System.out.println("showEditForm: Lỗi khi lấy sản phẩm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Model model,
            RedirectAttributes redirectAttributes) {
        System.out.println("updateProduct: Cập nhật sản phẩm, ID: " + id);
        System.out.println("updateProduct: Dữ liệu sản phẩm gửi lên: " + product);

        if (result.hasErrors()) {
            System.out.println("updateProduct: Lỗi validation: " + result.getAllErrors());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/productManage";
        }

        Optional<Product> existingProductOpt = productService.findById1(id);
        if (!existingProductOpt.isPresent()) {
            System.out.println("updateProduct: Không tìm thấy sản phẩm với ID: " + id);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/products";
        }

        try {
            Product existingProduct = existingProductOpt.get();
            product.setId(id);
            product.setCreateDate(existingProduct.getCreateDate());
            productService.update(product, mainImage, List.of(images != null ? images : new MultipartFile[0]));
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println("updateProduct: Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/productManage";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        System.out.println("deleteProduct: Xóa sản phẩm, ID: " + id);
        try {
            Optional<Product> product = productService.findById1(id);
            if (product.isPresent()) {
                List<ProductImage> productImages = productImageService.findByProductId(id);
                for (ProductImage image : productImages) {
                    fileUploadService.deleteFile(image.getImageUrl());
                    productImageService.deleteById(image.getId());
                }
                productService.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                System.out.println("deleteProduct: Không tìm thấy sản phẩm với ID: " + id);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            }
        } catch (Exception e) {
            System.out.println("deleteProduct: Lỗi khi xóa sản phẩm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @DeleteMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProductImage(@PathVariable("id") Integer id) {
        System.out.println("deleteProductImage: Xóa ảnh sản phẩm, Image ID: " + id);
        try {
            Optional<ProductImage> productImage = productImageService.findById(id);
            if (productImage.isPresent()) {
                fileUploadService.deleteFile(productImage.get().getImageUrl());
                productImageService.deleteById(id);
                return ResponseEntity.ok("Xóa ảnh thành công!");
            }
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh!");
        } catch (Exception e) {
            System.out.println("deleteProductImage: Lỗi khi xóa ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
}