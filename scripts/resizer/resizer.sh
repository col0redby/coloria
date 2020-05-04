#!/usr/bin/env bash

  # Example: ./resizer.sh colored-backend-eu-north-1 \
# nkharitonov/original/DSC_0835-small.jpg \
# colored-backend-eu-north-1 \
# images/user/nkharitonov \
# test_image.jpg \
# '50,200,600,1500'

# Check if we have enough arguments
if [ $# -ne 6 ]
  then
    echo "Usage: ./resizer.sh original_bucket original_key bucket key_prefix filename sizes_list"
    # exit 1
fi

# Unpack cli arguments
original_bucket=$1
original_key=$2
bucket=$3
key_prefix=$4
filename=$5
sizes_list=$6

# Crop image with ImageMagick and upload with aws cli
function process_size() {
  convert "$id" -resize "$1"x"$1"\> "${id}_$1"

  target_path="s3://$bucket/$key_prefix/$1/$filename"

  aws s3 cp --quiet --acl public-read "${id}_$1" $target_path
  echo "${1} $target_path"
}

# Generate unique id for process
id=$(date +%s.%N)

# Download s3 image
aws s3 cp --quiet "s3://$original_bucket/$original_key" "$id"

# Processing each passed size
for size in $(echo $sizes_list | tr ',' '\n') ; do
    process_size $size
done

# Remove all we created
rm "${id}"*
