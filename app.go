package main

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"html/template"
	"math/rand"
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

const (
	appID     = "YOUR_APP_ID_HERE"
	secretKey = "YOUR_SECRET_KEY_HERE"
)

func generateOrderID() string {
	chars := "ABCDEFGHJKLMNOPQRSTUVWXYZ23456789"
	stringLength := 6
	randomString := make([]byte, stringLength)
	rand.Seed(time.Now().UnixNano())

	for i := 0; i < stringLength; i++ {
		randomString[i] = chars[rand.Intn(len(chars))]
	}

	return "order_" + string(randomString)
}

func signatureRequest(data map[string]string, secretKey string) string {
	var keys []string
	for key := range data {
		keys = append(keys, key)
	}
	sort.Strings(keys)

	signatureData := ""
	for _, key := range keys {
		signatureData += key + data[key]
	}

	h := hmac.New(sha256.New, []byte(secretKey))
	h.Write([]byte(signatureData))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

func main() {
	r := gin.Default()

	// Set up HTML template rendering
	r.LoadHTMLGlob("templates/*")

	// Define route for the home page
	r.GET("/", func(c *gin.Context) {
		c.HTML(http.StatusOK, "index.html", nil)
	})

	// Define route for payment page
	r.GET("/pay", func(c *gin.Context) {
		formObj := map[string]string{
			"appId":         appID,
			"orderId":       generateOrderID(),
			"orderAmount":   "10",
			"orderCurrency": "USD",
			"orderNote":     "Add money to your wallet",
			"customerName":  "User",
			"customerPhone": "(607) 836-4935",
			"customerEmail": "john@doe.com",
			"returnUrl":     "", // PG returns to this URL after payment, Send Empty if not needed.
			"notifyUrl":     "", // PG sends a webhook to this URL after payment, Send Empty if not needed.
		}

		formObj["signature"] = signatureRequest(formObj, secretKey)

		c.HTML(http.StatusOK, "pay.html", gin.H{
			"form": formObj,
		})
	})

	// Run the application
	r.Run(":3399")
}
