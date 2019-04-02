package main
import (
     "github.com/intel-go/cpuid"
     "fmt"
     "strings"
)

func main() {
  	//fmt.Printf("Features: ")
  	for i := uint64(0); i < 64; i++ {
  		if cpuid.HasFeature(1 << i) {
  			fmt.Printf("%s ", strings.ToLower(cpuid.FeatureNames[1<<i]))
  		}
  	}
  	//fmt.Printf("\n")
  
  	//fmt.Printf("ExtendedFeatures: ")
  	for i := uint64(0); i < 64; i++ {
  		if cpuid.HasExtendedFeature(1 << i) {
  			fmt.Printf("%s ", strings.ToLower(cpuid.ExtendedFeatureNames[1<<i]))
  		}
  	}
  	//fmt.Printf("\n")
  
  	//fmt.Printf("ExtraFeatures: ")
  	for i := uint64(0); i < 64; i++ {
  		if cpuid.HasExtraFeature(1 << i) {
  			fmt.Printf("%s ", strings.ToLower(cpuid.ExtraFeatureNames[1<<i]))
  		}
  	}
  	//fmt.Printf("\n")
}
